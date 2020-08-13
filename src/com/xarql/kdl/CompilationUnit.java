package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlBaseListener;
import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.calculable.*;
import com.xarql.kdl.names.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.Text.nonNull;
import static com.xarql.kdl.names.InternalName.internalName;

public class CompilationUnit extends kdlBaseListener implements Runnable, CommonNames {
	public static final int CONST_ACCESS = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL;

	private static int unitCount = 0;

	private File   sourceFile;
	private String sourceCode;

	// set in constructor
	private final ClassWriter             cw;
	private final BestList<Import>        imports;
	private final BestList<JavaMethodDef> methods;
	private       Scope                   currentScope;
	private       CustomClass             clazz;
	private       boolean                 nameSet;
	private       int                     id;

	public final BestList<Constant>  constants;
	private final ConditionalHandler cmpHandler;
	private final BestList<String>   constantNames = new BestList<>();

	private String pkgName;

	// pass 1 collects imports, classname, and constant names, methodNames
	// pass 2 assigns values to constants
	// pass 3 defines methods
	private int pass;

	private CompilationUnit() {
		pass = 0;
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		constants = new BestList<>();
		imports = new BestList<>();
		methods = new BestList<>();
		cmpHandler = new ConditionalHandler(this);
		id = unitCount++;
	}

	public CompilationUnit(File sourceFile) {
		this();
		this.sourceFile = sourceFile;
	}

	public CompilationUnit(String sourceCode) {
		this();
		this.sourceCode = sourceCode;
	}

	public boolean runSilent() {
		try {
			// load source code
			if (sourceCode == null)
				sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
			compile();
			write();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void run() {
		try {
			// load source code
			if (sourceCode == null)
				sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
			compile();
			write();
			System.out.println("Compiled " + clazz.name);
		} catch (Exception e) {
			System.err.println("CompilationUnit " + unitName() + " aborted.");
			e.printStackTrace();
		}
	}

	public CompilationUnit compile() throws Exception {
		final ParseTree tree = makeParseTree(sourceCode);
		newPass();
		ParseTreeWalker.DEFAULT.walk(this, tree);
		newPass();
		ParseTreeWalker.DEFAULT.walk(this, tree);
		newPass();
		ParseTreeWalker.DEFAULT.walk(this, tree);
		cw.visitEnd();
		return this;
	}

	public String unitName() {
		if(clazz != null && clazz.name != null && !clazz.name.isEmpty())
			return clazz.name;
		else if(sourceFile != null)
			return sourceFile.getName();
		else
			return id + "";
	}

	private ParseTree makeParseTree(String input) throws Exception {
		SyntaxErrorHandler syntaxErrorHandler = new SyntaxErrorHandler(this);

		final com.xarql.kdl.antlr4.kdlLexer lex = new com.xarql.kdl.antlr4.kdlLexer(CharStreams.fromString(input));
		lex.removeErrorListeners();
		lex.addErrorListener(syntaxErrorHandler);

		final CommonTokenStream tokens = new CommonTokenStream(lex);

		final kdlParser parser = new kdlParser(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(syntaxErrorHandler);

		final ParseTree tree = parser.source();

		if(syntaxErrorHandler.hasErrors()) {
			syntaxErrorHandler.printErrors();
			throw new Exception("Encountered syntax errors while parsing.");
		}

		return tree;
	}

	public CompilationUnit write(Path destination) throws IOException {
		Files.write(destination, cw.toByteArray());
		return this;
	}

	public CompilationUnit write() throws IOException, NullPointerException {
		if(sourceFile == null)
			throw new NullPointerException("write() without params in CompilationUnit if the unit wasn't created with a file.");

		return write(sourceFile.toPath().resolveSibling(clazz.name + ".class"));
	}

	private static NameAndType parseTypedVariable(kdlParser.TypedVariableContext ctx) {
		String name = ctx.VARNAME().toString();

		InternalName type;
		if(ctx.type().basetype().BOOLEAN() != null) {
			type = BOOLEAN_IN;
		}
		else if(ctx.type().basetype().INT() != null) {
			type = INT_IN;
		}
		else if(ctx.type().basetype().STRING() != null) {
			type = STRING_IN;
		}
		else
			throw new IllegalArgumentException("Couldn't resolve type of variable");

		return new NameAndType(name, type);
	}

	/**
	 * Converts the top item of the stack in to a string
	 * @param name
	 * @param lmv
	 */
	public static void convertToString(InternalObjectName name, LinedMethodVisitor lmv) {
		JavaMethodDef stringValueOf;
		if(name.isBaseType())
			if(name.toBaseType() == STRING)
				return;
			else
				stringValueOf = new JavaMethodDef(STRING_IN, "valueOf", list(name), new ReturnValue(String.class), ACC_PUBLIC + ACC_STATIC);
		else
			stringValueOf = new JavaMethodDef(STRING_IN, "valueOf", list(new InternalObjectName(Object.class)), new ReturnValue(String.class), ACC_PUBLIC + ACC_STATIC);
		lmv.visitMethodInsn(INVOKESTATIC, stringValueOf.owner(), stringValueOf.methodName, stringValueOf.descriptor(), false);
	}

	/**
	 * Store the value that is at the top of the stack in the target variable
	 * @param type the type of the data on top of the stack
	 * @param target variable in which data will be stored
	 * @param lmv any LinedMethodVisitor
	 */
	private static void store(ToName type, Variable target, LinedMethodVisitor lmv) throws Exception {
		if(!type.toInternalName().equals(target.toInternalName()))
			throw new IncompatibleTypeException(type + INCOMPATIBLE + target);
		else {
			if(type.toBaseType() == INT || type.toBaseType() == BOOLEAN)
				lmv.visitVarInsn(ISTORE, target.localIndex);
			else
				lmv.visitVarInsn(ASTORE, target.localIndex);
		}
	}

	private static void storeDefault(Variable lv, LinedMethodVisitor lmv) {
		if(lv.type.isBaseType()) {
			switch(lv.type.toBaseType()) {
				case INT:
				case BOOLEAN:
					lmv.visitLdcInsn(DEFAULT_INT);
					lmv.visitVarInsn(ISTORE, lv.localIndex);
					break;
				case STRING:
					lmv.visitLdcInsn(DEFAULT_STRING);
					lmv.visitVarInsn(ASTORE, lv.localIndex);
					break;
			}
		}
		else {
			lmv.visitInsn(ACONST_NULL);
			lmv.visitVarInsn(ASTORE, lv.localIndex);
		}
	}

	private static Operator parseOperator(kdlParser.OperatorContext ctx) {
		return Operator.match(ctx.getText());
	}


	@Override
	public void enterPkg(kdlParser.PkgContext ctx) {
		pkgName = ctx.PKG_NAME().getText();
	}

	public int getPass() {
		return pass;
	}

	public void newPass() {
		pass++;
	}

	@Override
	public void enterSee(kdlParser.SeeContext ctx) {
		if(getPass() == 1)
			addImport(new Import(ctx.QUALIFIED_NAME().toString()));
	}

	@Override
	public void enterClazz(final kdlParser.ClazzContext clazzCtx) {
		if(getPass() == 1) {
			pkgName = nonNull(pkgName);
			setClassName(pkgName, clazzCtx.CLASSNAME().toString());
		}
	}

	@Override
	public void enterConstant(final kdlParser.ConstantContext ctx) {
		// collect names
		if(getPass() == 1) {
			final String name = ctx.CONSTNAME().toString();
			if(!constantNames.contains(name))
				constantNames.add(name);
			else
				throw new IllegalArgumentException("The const named " + name + " was taken by another const with value " + getConstant(name).value);
		}
		else if(getPass() == 2) {
			final String name = ctx.CONSTNAME().getText();
			if(!constantNames.contains(name))
				throw new IllegalArgumentException("The const named " + name + " was not registered in the first pass.");
			final Literal lit = Literal.parseLiteral(ctx.literal());
			addConstant(new Constant(name, lit.value));
		}
	}

	private void consumeMethodCall(kdlParser.MethodCallContext ctx, LinedMethodVisitor lmv) throws Exception {
		new MethodCall(ctx, this).push(lmv);
	}

	public void consumeStatementSet(final kdlParser.StatementSetContext ctx, LinedMethodVisitor lmv) throws Exception {
		if(ctx.statement() != null)
			consumeStatement(ctx.statement(), lmv);
		else
			consumeBlock(ctx.block(), lmv);
	}

	private void consumeVariableDeclaration(kdlParser.VariableDeclarationContext ctx, LinedMethodVisitor lmv) throws Exception {
		NameAndType details = parseTypedVariable(ctx.typedVariable());
		Variable var = new Variable(currentScope, details.name, details.type.toInternalObjectName());

		if(ctx.ASSIGN() != null)
			store(Resolvable.parse(this, ctx.expression().value(0)).push(lmv), var, lmv);
		else
			storeDefault(var, lmv);
	}

	private void consumeVariableAssignment(kdlParser.VariableAssignmentContext ctx, LinedMethodVisitor lmv) throws Exception {
		Variable target = getLocalVariable(ctx.VARNAME().getText());
		if(ctx.assignment().operatorAssign() != null) {
			ExpressionHandler.compute(new Expression(getLocalVariable(ctx.VARNAME().getText()), Resolvable.parse(this, ctx.assignment().operatorAssign().value()), Operator.match(ctx.assignment().operatorAssign().operator().getText())), lmv);
		}
		else {
			ToName resultType = ExpressionHandler.compute(new Expression(ctx.assignment().expression(), this), lmv);
			store(resultType, target, lmv);
		}
	}

	private void consumeStatement(final kdlParser.StatementContext ctx, LinedMethodVisitor lmv) throws Exception {
		if(ctx.variableDeclaration() != null) {
			consumeVariableDeclaration(ctx.variableDeclaration(), lmv);
		}
		else if(ctx.variableAssignment() != null) {
			consumeVariableAssignment(ctx.variableAssignment(), lmv);
		}
		else if(ctx.methodCall() != null) {
			consumeMethodCall(ctx.methodCall(), lmv);
		}
		else if(ctx.conditional() != null) {
			// forward to the handler to partition code
			cmpHandler.handle(ctx.conditional(), lmv);
		}
		else
			throw new UnimplementedException("A type of statement couldn't be interpreted " + ctx.getText());
	}

	private void consumeBlock(final kdlParser.BlockContext ctx, LinedMethodVisitor lmv) throws Exception {
		for (kdlParser.StatementContext statement : ctx.statement())
			consumeStatement(statement, lmv);
	}

	@Override
	public void enterRun(final kdlParser.RunContext ctx) {
		if(getPass() == 2) {
			addMethodDef(JavaMethodDef.MAIN.withOwner(clazz));
		}
		else if(getPass() == 3) {
			ExternalMethodRouter.writeMethods(this);

			final LinedMethodVisitor lmv = defineMethod(JavaMethodDef.MAIN, ctx.start.getLine() + 1);
			new Variable(currentScope, "args", new InternalObjectName(String.class, 1));
			Label methodStart = new Label();
			try {
				consumeBlock(ctx.block(), lmv);
			} catch (Exception e) {
				System.err.println("From unit: " + unitName());
				e.printStackTrace();
			}

			final Label ret = new Label();
			lmv.visitLabel(ret);
			lmv.visitLineNumber(ctx.start.getLine(), ret);
			lmv.visitInsn(Opcodes.RETURN);

			final Label methodEnd = new Label();
			lmv.visitLabel(methodEnd);
			for(Variable lv : currentScope.getVariables())
				lmv.visitLocalVariable(lv.name, lv.type.toString(), null, methodStart, methodEnd, lv.localIndex);
			lmv.visitMaxs(0, 0);
			lmv.visitEnd();
		}
	}

	public Variable getLocalVariable(final String name) {
		return currentScope.getVariable(name.trim());
	}

	public void addMethodDef(JavaMethodDef md) {
		methods.add(md);
	}

	public BestList<JavaMethodDef> getMethods() {
		return methods;
	}

	public boolean hasConstant(final String name) {
		for(Constant c : constants) {
			if(c.name.equals(name))
				return true;
		}
		return false;
	}

	public Constant getConstant(final String name) {
		for(Constant c : constants) {
			if(c.name.equals(name))
				return c;
		}
		throw new IllegalArgumentException("Constant " + name + " does not exist");
	}

	public boolean isConstantsSet() {
		for(Constant c : constants)
			if(!c.isEvaluated())
				return false;
		return true;
	}

	/**
	 * Sets the name of this class to the given className.
	 * @param name name of class, Ex. Test
	 * @return success of operation
	 */
	public boolean setClassName(final String pkg, final String name) {
		if(!nameSet) {
			this.clazz = new CustomClass(pkg, name);
			nameSet = true;

			// give name to ClassWriter
			cw.visit(Opcodes.V1_6, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, clazz.internalNameString(), null, internalName(Object.class).toString(), null);
			cw.visitSource(clazz + ".kdl", null);

			return true;
		}
		else
			return false;
	}

	public CustomClass getClazz() {
		return clazz;
	}

	public LinedMethodVisitor defineMethod(JavaMethodDef md, int line) {
		if(methods.contains(md)) {
			currentScope = new Scope("Method " + md.methodName + " of class " + clazz, new Label());
			final MethodVisitor mv = cw.visitMethod(md.access, md.methodName, md.descriptor(), null, null);
			mv.visitCode();
			return new LinedMethodVisitor(mv, line);
		}
		else
			throw new IllegalArgumentException("None of the detected method definitions match the given method definition");
	}

	public void addImport(final Import imp) {
		imports.add(imp);
	}

	public boolean addConstant(final Constant<?> c) {
		if(constants.contains(c))
			return false;
		FieldVisitor fv;
		if(c.value instanceof String)
			fv = cw.visitField(CONST_ACCESS, c.name, new InternalObjectName(String.class).toString(), null, c.value.toString());
		else if(c.value instanceof Boolean)
			fv = cw.visitField(CONST_ACCESS, c.name, BaseType.BOOLEAN.stringOutput(), null, c.value);
		else if(c.value instanceof Integer)
			fv = cw.visitField(CONST_ACCESS, c.name, BaseType.INT.stringOutput(), null, c.value);
		else
			throw new UnsupportedOperationException("The class " + c.value.getClass() + " could not be resolved to a const type");
		fv.visitEnd();
		return constants.add(c);
	}

	public void addDefaultConstructor(final ClassWriter cw) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(3, l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", clazz.internalObjectNameString(), null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public Scope getCurrentScope() {
		if(currentScope == null)
			currentScope = new Scope("default");
		return currentScope;
	}

}
