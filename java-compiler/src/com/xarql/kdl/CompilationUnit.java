package com.xarql.kdl;

import com.xarql.kdl.antlr.kdlBaseListener;
import com.xarql.kdl.antlr.kdlLexer;
import com.xarql.kdl.antlr.kdl;
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

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.Text.nonNull;
import static com.xarql.kdl.names.InternalName.internalName;

public class CompilationUnit extends kdlBaseListener implements Runnable, CommonNames {
	public static final int CONST_ACCESS = ACC_PUBLIC + ACC_STATIC + ACC_FINAL;
	public static final String INCORRECT_FILE_NAME = "The input file name must match its class name.";

	private static int unitCount = 0;

	private File   sourceFile;
	private String sourceCode;
	private File   outputFile;

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

	public CompilationUnit(File sourceFile, File outputFile) {
		this();
		this.sourceFile = sourceFile;
		this.outputFile = outputFile;
	}

	public CompilationUnit(File sourceFile) {
		this(sourceFile, null);
	}

	public CompilationUnit(String sourceCode) {
		this();
		this.sourceCode = sourceCode;
	}

	public void runSilent() throws Exception {
		try {
			// load source code
			if (sourceCode == null)
				sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
			compile();
			write();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void run() {
		try {
			// load source code
			if (sourceCode == null)
				sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
			compile();
			if(outputFile != null)
				write(outputFile);
			else
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

		final kdlLexer lex = new kdlLexer(CharStreams.fromString(input));
		lex.removeErrorListeners();
		lex.addErrorListener(syntaxErrorHandler);

		final CommonTokenStream tokens = new CommonTokenStream(lex, 0);

		final kdl parser = new kdl(tokens);
		parser.removeErrorListeners();
		parser.addErrorListener(syntaxErrorHandler);

		final ParseTree tree = parser.source();

		if(syntaxErrorHandler.hasErrors()) {
			syntaxErrorHandler.printErrors();
			throw new Exception("Encountered syntax errors while parsing.");
		}

		return tree;
	}

	public CompilationUnit write(File destination) throws IOException {
		// check input file name
		if(sourceFile != null && !sourceFile.getName().replace(".kdl", "").equalsIgnoreCase(clazz.name))
			throw new IllegalArgumentException(INCORRECT_FILE_NAME + " file:" + sourceFile.getName() + " class:" + clazz.name);

		// if destination is a directory, make a file within that directory
		if(destination.isDirectory())
			destination = new File(destination, clazz.name + ".class");

		Files.write(destination.toPath(), cw.toByteArray());
		return this;
	}

	public CompilationUnit write() throws IOException, NullPointerException {
		if(sourceFile == null)
			throw new NullPointerException("write() without params in CompilationUnit if the unit wasn't created with a file.");
		return write(new File(sourceFile.toPath().resolveSibling(clazz.name + ".class").toString()));
	}

	private static NameAndType parseTypedVariable(kdl.TypedVariableContext ctx) {
		String name = ctx.VARNAME().getText();

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
	 * @param visitor
	 */
	public static void convertToString(InternalObjectName name, MethodVisitor visitor) {
		JavaMethodDef stringValueOf;
		if(name.isBaseType())
			if(name.toBaseType() == STRING)
				return;
			else
				stringValueOf = new JavaMethodDef(STRING_IN, "valueOf", list(name), ReturnValue.STRING_RETURN, ACC_PUBLIC + ACC_STATIC);
		else
			stringValueOf = new JavaMethodDef(STRING_IN, "valueOf", list(new InternalObjectName(Object.class)), ReturnValue.STRING_RETURN, ACC_PUBLIC + ACC_STATIC);
		visitor.visitMethodInsn(INVOKESTATIC, stringValueOf.owner(), stringValueOf.methodName, stringValueOf.descriptor(), false);
	}

	/**
	 * Store the value that is at the top of the stack in the target variable
	 * @param type the type of the data on top of the stack
	 * @param target variable in which data will be stored
	 * @param lmv any MethodVisitor
	 */
	public static void store(ToName type, Variable target, MethodVisitor lmv) throws Exception {
		if(!type.toInternalName().equals(target.toInternalName()))
			throw new IncompatibleTypeException(type + INCOMPATIBLE + target);
		else {
			if(type.toBaseType() == INT || type.toBaseType() == BOOLEAN)
				lmv.visitVarInsn(ISTORE, target.localIndex);
			else
				lmv.visitVarInsn(ASTORE, target.localIndex);
		}
	}

	public static void storeDefault(Variable lv, MethodVisitor lmv) {
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

	private static Operator parseOperator(kdl.OperatorContext ctx) {
		return Operator.match(ctx.getText());
	}


	@Override
	public void enterPkg(kdl.PkgContext ctx) {
		pkgName = ctx.PKG_NAME().getText();
	}

	public int getPass() {
		return pass;
	}

	public void newPass() {
		pass++;
	}

	@Override
	public void enterClazz(final kdl.ClazzContext ctx) {
		if(getPass() == 1) {
			pkgName = nonNull(pkgName);
			setClassName(pkgName, ctx.CLASSNAME().toString());
			ExternalMethodRouter.writeMethods(this, ctx.start.getLine());
			addDefaultConstructor(cw);
		}
	}

	@Override
	public void enterConstant(final kdl.ConstantContext ctx) {
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

	private void consumeMethodCall(kdl.MethodCallContext ctx, MethodVisitor lmv) throws Exception {
		new MethodCall(ctx, this).calc(lmv);
	}

	private void consumeVariableDeclaration(kdl.VariableDeclarationContext ctx, MethodVisitor lmv) throws Exception {
		NameAndType details = parseTypedVariable(ctx.typedVariable());
		Variable var = new Variable(currentScope, details.name, details.type.toInternalObjectName());

		if(ctx.ASSIGN() != null)
			store(new Expression(ctx.expression(), this).calc(lmv), var, lmv);
		else
			storeDefault(var, lmv);
	}

	private void consumeVariableAssignment(kdl.VariableAssignmentContext ctx, MethodVisitor lmv) throws Exception {
		Variable target = getLocalVariable(ctx.VARNAME().getText());
		final ToName resultType;
		if(ctx.assignment().operatorAssign() != null)
			resultType = ExpressionHandler.compute(new Expression(getLocalVariable(ctx.VARNAME().getText()), Resolvable.parse(this, ctx.assignment().operatorAssign().value()), Operator.match(ctx.assignment().operatorAssign().operator().getText())), lmv);
		else
			resultType = ExpressionHandler.compute(new Expression(ctx.assignment().expression(), this), lmv);
		store(resultType, target, lmv);
	}

	public void consumeStatement(final kdl.StatementContext ctx, MethodVisitor lmv) throws Exception {
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
			cmpHandler.handle(ctx.conditional(), lmv, this);
		}
		else if(ctx.returnStatement() != null) {
			if(ctx.returnStatement().expression() == null) {
				lmv.visitInsn(RETURN);
				return;
			}

			ToName returnType = ExpressionHandler.compute(new Expression(ctx.returnStatement().expression(), this), lmv);
			if(returnType.isBaseType()) {
				switch(returnType.toBaseType()) {
					case BOOLEAN:
					case INT:
						lmv.visitInsn(IRETURN);
						break;
					case STRING:
						lmv.visitInsn(ARETURN);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			}
			else
				lmv.visitInsn(ARETURN);
		}
		else
			throw new UnimplementedException("A type of statement couldn't be interpreted " + ctx.getText());
	}

	public void consumeBlock(final kdl.BlockContext ctx, MethodVisitor lmv) throws Exception {
		for (kdl.StatementContext statement : ctx.statement())
			consumeStatement(statement, lmv);
	}

	@Override
	public void enterMethodDefinition(final kdl.MethodDefinitionContext ctx) {
		// parse name and return type
		final NameAndType details;
		if(ctx.typedVariable() != null)
			details = parseTypedVariable(ctx.typedVariable());
		else
			details = new NameAndType(ctx.VARNAME().getText(), null);
		final ReturnValue rv = ReturnValue.returnValue(details.type);

		// parse parameters
		final BestList<NameAndType> params = new BestList<>();
		for(kdl.TypedVariableContext typedVar : ctx.parameterDefinition().typedVariable())
			params.add(parseTypedVariable(typedVar));

		// create MethodDef
		final BestList<InternalObjectName> paramTypes = new BestList<>();
		for(NameAndType param : params)
			paramTypes.add(param.type.toInternalObjectName());
		MethodDef def = new MethodDef(new InternalName(clazz), MethodDef.Type.FNC, details.name, paramTypes, rv, ACC_PUBLIC + ACC_STATIC);

		if(pass == 2) {
			addMethodDef(def);
		} else if(pass == 3) {
			Label methodStart = new Label();
			final MethodVisitor visitor = defineMethod(def);
			for(NameAndType param : params)
				new Variable(currentScope, param.name, param.type.toInternalObjectName());

			try {
				consumeBlock(ctx.block(), visitor);
			} catch (Exception e) {
				System.err.println("From unit: " + unitName());
				e.printStackTrace();
			}

			getCurrentScope().end(ctx.stop.getLine(), visitor, rv);
		}
	}

	@Override
	public void enterMain(final kdl.MainContext ctx) {
		if(getPass() == 2) {
			addMethodDef(JavaMethodDef.MAIN.withOwner(clazz));
		}
		else if(getPass() == 3) {
			Label methodStart = new Label();
			final MethodVisitor mv = defineMethod(JavaMethodDef.MAIN);
			new Variable(currentScope, "args", new InternalObjectName(String.class, 1));
			try {
				consumeBlock(ctx.block(), mv);
			} catch (Exception e) {
				System.err.println("From unit: " + unitName());
				e.printStackTrace();
			}

			getCurrentScope().end(ctx.stop.getLine(), mv, ReturnValue.VOID);
		}
	}

	public Variable getLocalVariable(final String name) {
		return currentScope.getVariable(name.trim());
	}

	public void addMethodDef(JavaMethodDef md) {
		if(methods.contains(md))
			throw new IllegalArgumentException("The method " + md + " already exists in " + unitName());
		else
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
			cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, clazz.internalNameString(), null, internalName(Object.class).toString(), null);
			cw.visitSource(clazz + ".kdl", null);

			return true;
		}
		else
			return false;
	}

	public CustomClass getClazz() {
		return clazz;
	}

	public MethodVisitor defineMethod(JavaMethodDef md) {
		if(methods.contains(md)) {
			final MethodVisitor mv = cw.visitMethod(md.access, md.methodName, md.descriptor(), null, null);
			currentScope = new Scope("Method " + md.methodName + " of class " + clazz, mv);
			mv.visitCode();
			return mv;
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
		final MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		final Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(3, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		final Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", clazz.internalObjectNameString(), null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public Scope getCurrentScope() {
		return currentScope;
	}

}
