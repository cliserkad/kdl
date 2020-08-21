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
import java.lang.reflect.Method;
import java.nio.file.Files;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.Text.nonNull;
import static com.xarql.kdl.names.BaseType.*;

public class CompilationUnit extends kdlBaseListener implements Runnable, CommonText {
	public static final int CONST_ACCESS = ACC_PUBLIC + ACC_STATIC + ACC_FINAL;
	public static final String INCORRECT_FILE_NAME = "The input file name must match its class name.";

	private static int unitCount = 0;

	private File   sourceFile;
	private String sourceCode;
	private File   outputFile;

	// set in constructor
	private final ClassWriter             cw;
	private final BestList<InternalName>  imports;
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
		addImport(String.class);
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

	private NameAndType parseTypedVariable(kdl.TypedVariableContext ctx) throws Exception {
		String name = ctx.VARNAME().getText();

		InternalName type = null;
		if(ctx.type().basetype() != null) {
			if(ctx.type().basetype().BOOLEAN() != null)
				type = InternalName.BOOLEAN;
			else if(ctx.type().basetype().BYTE() != null)
				type = InternalName.BYTE;
			else if(ctx.type().basetype().SHORT() != null)
				type = InternalName.SHORT;
			else if(ctx.type().basetype().CHAR() != null)
				type = InternalName.CHAR;
			else if(ctx.type().basetype().INT() != null)
				type = InternalName.INT;
			else if(ctx.type().basetype().FLOAT() != null)
				type = InternalName.FLOAT;
			else if(ctx.type().basetype().LONG() != null)
				type = InternalName.LONG;
			else if(ctx.type().basetype().DOUBLE() != null)
				type = InternalName.DOUBLE;
			else if (ctx.type().basetype().STRING() != null)
				type = InternalName.STRING;
			else
				throw new UnimplementedException(SWITCH_BASETYPE);
		}
		else {
			type = resolveAgainstImports(ctx.type().getText());
			if(type == null)
				throw new IllegalArgumentException("Couldn't recognize type");
		}

		return new NameAndType(name, type);
	}

	public InternalName resolveAgainstImports(String src) {
		for(InternalName in : imports) {
			String str = in.internalName();
			if(str.contains("/") && str.lastIndexOf("/") + 1 <= str.length() && str.substring(str.lastIndexOf("/") + 1).equals(src)) {
				return in;
			}
		}
		throw new IllegalArgumentException("Couldn't recognize type");
	}

	/**
	 * Converts the top item of the stack in to a string
	 * @param name
	 * @param visitor
	 */
	public static void convertToString(final InternalName name, final MethodVisitor visitor) {
		JavaMethodDef stringValueOf;
		if(name.isBaseType()) {
			if(name.toBaseType() == STRING)
				return;
			else {
				InternalName actualName = name;
				if(name.toBaseType() == BYTE)
					actualName = InternalName.INT;
				else if(name.toBaseType() == SHORT)
					actualName = InternalName.INT;
				stringValueOf = new JavaMethodDef(InternalName.STRING, "valueOf", list(actualName), ReturnValue.STRING, ACC_PUBLIC + ACC_STATIC);
			}
		}
		else
			stringValueOf = new JavaMethodDef(InternalName.STRING, "valueOf", list(InternalName.OBJECT), ReturnValue.STRING, ACC_PUBLIC + ACC_STATIC);
		visitor.visitMethodInsn(INVOKESTATIC, stringValueOf.owner(), stringValueOf.methodName, stringValueOf.descriptor(), false);
	}

	/**
	 * Store the value that is at the top of the stack in the target variable
	 * @param type the type of the data on top of the stack
	 * @param target variable in which data will be stored
	 * @param lmv any MethodVisitor
	 */
	public static void store(ToName type, Variable target, MethodVisitor lmv) throws Exception {
		if(!type.toInternalName().compatibleWith(target.type))
			throw new IncompatibleTypeException(type + INCOMPATIBLE + target);
		else {
			if(type.isBaseType()) {
				// convert integer to long
				if(target.toBaseType() == BaseType.LONG && type.toBaseType() == BaseType.INT)
					lmv.visitInsn(I2L);
				// convert float to double
				if(target.toBaseType() == BaseType.DOUBLE && type.toBaseType() == BaseType.FLOAT)
					lmv.visitInsn(F2D);
			}

			if(target.isBaseType()) {
				switch (target.toBaseType()) {
					case BOOLEAN:
					case BYTE:
					case SHORT:
					case CHAR:
					case INT:
						lmv.visitVarInsn(ISTORE, target.localIndex);
						break;
					case FLOAT:
						lmv.visitVarInsn(FSTORE, target.localIndex);
						break;
					case LONG:
						lmv.visitVarInsn(LSTORE, target.localIndex);
						break;
					case DOUBLE:
						lmv.visitVarInsn(DSTORE, target.localIndex);
						break;
					case STRING:
						lmv.visitVarInsn(ASTORE, target.localIndex);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			}
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
			if(!pkgName.isEmpty())
				setClassName(pkgName.substring(0, pkgName.lastIndexOf(".") + 1), ctx.CLASSNAME().getText());
			else
				setClassName(pkgName, ctx.CLASSNAME().getText());
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
			try {
				final Literal lit = Literal.parseLiteral(ctx.literal());
				addConstant(new Constant(name, lit.value));
			} catch(Exception e) {
				printException(e);
			}
		}
	}

	private void consumeMethodCallStatement(kdl.MethodCallStatementContext ctx, MethodVisitor visitor) throws Exception {
		kdl.MethodCallContext mcc = ctx.methodCall();
		if(mcc.VARNAME().size() > 1)
			getLocalVariable(mcc.VARNAME(0).getText()).push(visitor);
		new MethodCall(ctx, this).calc(visitor);
	}

	private void consumeNewObject(kdl.NewObjectContext ctx, MethodVisitor visitor) throws Exception {
		new NewObject(ctx, this).calc(visitor);
	}

	private void consumeVariableDeclaration(kdl.VariableDeclarationContext ctx, MethodVisitor lmv) throws Exception {
		final NameAndType details = parseTypedVariable(ctx.typedVariable());
		Variable var = getCurrentScope().newVariable(details.name, details.type);

		if(ctx.ASSIGN() != null) {
			store(new Expression(ctx.expression(), this).calc(lmv), var, lmv);
		}
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

	public void consumeStatement(final kdl.StatementContext ctx, MethodVisitor visitor) throws Exception {
		if(ctx.variableDeclaration() != null) {
			consumeVariableDeclaration(ctx.variableDeclaration(), visitor);
		}
		else if(ctx.variableAssignment() != null) {
			consumeVariableAssignment(ctx.variableAssignment(), visitor);
		}
		else if(ctx.methodCallStatement() != null) {
			consumeMethodCallStatement(ctx.methodCallStatement(), visitor);
		}
		else if(ctx.conditional() != null) {
			// forward to the handler to partition code
			cmpHandler.handle(ctx.conditional(), visitor, this);
		}
		else if(ctx.returnStatement() != null) {
			if(ctx.returnStatement().expression() == null) {
				visitor.visitInsn(RETURN);
				return;
			}

			ToName returnType = ExpressionHandler.compute(new Expression(ctx.returnStatement().expression(), this), visitor);
			if(returnType.isBaseType()) {
				switch(returnType.toBaseType()) {
					case BOOLEAN:
					case INT:
						visitor.visitInsn(IRETURN);
						break;
					case STRING:
						visitor.visitInsn(ARETURN);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			}
			else
				visitor.visitInsn(ARETURN);
		}
		else if(ctx.newObject() != null) {
			consumeNewObject(ctx.newObject(), visitor);
		}
		else
			throw new UnimplementedException("A type of statement couldn't be interpreted " + ctx.getText());
	}

	public void consumeBlock(final kdl.BlockContext ctx, MethodVisitor lmv) throws Exception {
		for (kdl.StatementContext statement : ctx.statement())
			consumeStatement(statement, lmv);
	}

	@Override
	public void enterPath(final kdl.PathContext ctx) {
		if(pass == 1)
			pkgName = ctx.getText().trim().substring(4).trim();
	}

	@Override
	public void enterUse(final kdl.UseContext ctx) {
		if(pass == 1) {
			addImport(ctx.getText().substring(3, ctx.getText().length() - 1));
		}
	}

	public void addImport(String text) {
		try {
			final Class<?> jvmClass = Class.forName(text);
			addImport(jvmClass);
		} catch(Exception e) {
			printException(e);
		}
	}

	public void addImport(Class<?> clazz) {
		imports.add(new InternalName(clazz));
		for(Method method : clazz.getMethods()) {
			methods.add(new JavaMethodDef(clazz, method));
		}
	}

	@Override
	public void enterMethodDefinition(final kdl.MethodDefinitionContext ctx) {
		try {
			// parse name and return type
			final NameAndType details;
			if (ctx.typedVariable() != null)
				details = parseTypedVariable(ctx.typedVariable());
			else
				details = new NameAndType(ctx.VARNAME().getText(), null);
			final ReturnValue rv = new ReturnValue(details.type);

			// parse parameters
			final BestList<NameAndType> params = new BestList<>();
			for (kdl.TypedVariableContext typedVar : ctx.parameterDefinition().typedVariable())
				params.add(parseTypedVariable(typedVar));

			// create MethodDef
			final BestList<InternalName> paramTypes = new BestList<>();
			for (NameAndType param : params)
				paramTypes.add(param.type);
			MethodDef def = new MethodDef(new InternalName(clazz), MethodDef.Type.FNC, details.name, paramTypes, rv, ACC_PUBLIC + ACC_STATIC);

			if(pass == 2) {
				addMethodDef(def);
			} else if(pass == 3) {
				Label methodStart = new Label();
				final MethodVisitor visitor = defineMethod(def);
				for(NameAndType param : params)
					getCurrentScope().newVariable(param.name, param.type);
				consumeBlock(ctx.block(), visitor);

				getCurrentScope().end(ctx.stop.getLine(), visitor, rv);
			}
		} catch(Exception e) {
			printException(e);
		}
	}

	private void printException(final Exception e) {
		System.err.println("From unit: " + unitName());
		e.printStackTrace();
	}

	@Override
	public void enterMain(final kdl.MainContext ctx) {
		if(getPass() == 2) {
			addMethodDef(JavaMethodDef.MAIN.withOwner(clazz));
		}
		else if(getPass() == 3) {
			final MethodVisitor mv = defineMethod(JavaMethodDef.MAIN);
			getCurrentScope().newVariable("args", new InternalName(String.class, 1));
			try {
				consumeBlock(ctx.block(), mv);
			} catch (Exception e) {
				printException(e);
			}
			getCurrentScope().end(ctx.stop.getLine(), mv, ReturnValue.VOID);
		}
	}

	public Variable getLocalVariable(final String name) {
		return getCurrentScope().getVariable(name.trim());
	}

	public JavaMethodDef addMethodDef(JavaMethodDef md) {
		if(methods.contains(md))
			throw new IllegalArgumentException("The method " + md + " already exists in " + unitName());
		else
			methods.add(md);
		return md;
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
			cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, clazz.internalNameString(), null, InternalName.OBJECT.internalName(), null);
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

	public boolean addConstant(final Constant<?> c) {
		if(constants.contains(c))
			return false;
		FieldVisitor fv;
		if(c.value instanceof String)
			fv = cw.visitField(CONST_ACCESS, c.name, new InternalName(String.class).internalObjectName(), null, c.value.toString());
		else if(c.value instanceof Boolean)
			fv = cw.visitField(CONST_ACCESS, c.name, BOOLEAN.stringOutput(), null, c.value);
		else if(c.value instanceof Integer)
			fv = cw.visitField(CONST_ACCESS, c.name, INT.stringOutput(), null, c.value);
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
