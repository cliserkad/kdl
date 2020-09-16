package com.xarql.kdl;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.Text.nonNull;
import static com.xarql.kdl.names.BaseType.BYTE;
import static com.xarql.kdl.names.BaseType.SHORT;
import static com.xarql.kdl.names.BaseType.STRING;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.antlr.kdlBaseListener;
import com.xarql.kdl.antlr.kdlLexer;
import com.xarql.kdl.ir.ConditionalHandler;
import com.xarql.kdl.ir.Constant;
import com.xarql.kdl.ir.Expression;
import com.xarql.kdl.ir.ExpressionHandler;
import com.xarql.kdl.ir.Literal;
import com.xarql.kdl.ir.MethodCall;
import com.xarql.kdl.ir.NewObject;
import com.xarql.kdl.ir.Operator;
import com.xarql.kdl.ir.Pushable;
import com.xarql.kdl.ir.Variable;
import com.xarql.kdl.ir.VariableAssignment;
import com.xarql.kdl.ir.VariableDeclaration;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import com.xarql.kdl.names.ToName;

public class CompilationUnit extends kdlBaseListener implements Runnable, CommonText {

	public static final int CONST_ACCESS = ACC_PUBLIC + ACC_STATIC + ACC_FINAL;
	public static final String INCORRECT_FILE_NAME = "The input file name must match its class name.";

	private static int unitCount = 0;

	private File sourceFile;
	private String sourceCode;
	private File outputFile;

	// set in constructor
	private final ClassWriter cw;
	private final BestList<InternalName> imports;
	private final BestList<JavaMethodDef> methods;
	private Scope currentScope;
	private CustomClass clazz;
	private boolean nameSet;
	private final int id;

	public final BestList<Constant<?>> constants;
	private final ConditionalHandler cmpHandler;
	private final BestList<String> constantNames = new BestList<>();
	private final Map<String, kdl.ConstantContext> constantContexts = new HashMap<>();

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
		// load source code
		if(sourceCode == null)
			sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
		compile();
		write();
	}

	@Override
	public void run() {
		try {
			// load source code
			if(sourceCode == null)
				sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
			compile(;
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

	public Details parseTypedVariable(kdl.TypedVariableContext ctx) throws Exception {
		String name = ctx.VARNAME().getText();

		InternalName type;
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
			else if(ctx.type().basetype().STRING() != null)
				type = InternalName.STRING;
			else
				throw new UnimplementedException(SWITCH_BASETYPE);
		} else {
			type = resolveAgainstImports(ctx.type().getText());
			if(type == null)
				thrownew IllegalArgumentException("Couldn't recognize type");
		}

		if(ctx.type().BRACE_OPEN() != null) {
			int dimensions = 0;
			for (int i = 0; i < ctx.type().BRACE_OPEN().size(); i++)
				dimensions++;
			type = type.toArray(dimensions)
		}

		return new Details(name, type, ctx.MUTABLE() != null);
	}

	public InternalName resolveAgainstImports(String src) {
		for (InternalName in : imports) {
			String str = in.nameString();
			if(str.contains("/") && str.lastIndexOf("/") + 1 <= str.length() && str.substring(str.lastIndexOf("/") + 1).equals(src)) {
				return in;
			}
		}
		throw new IllegalArgumentException("Couldn't recognize type");
	}

	/**
	 * Converts the top item of the stack in to a string
	 * 
	 * @param name    The type of the element on the stack
	 * @param visitor Any MethodVisitor
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
		} else
			stringValueOf = new JavaMethodDef(InternalName.STRING, "valueOf", list(InternalName.OBJECT), ReturnValue.STRING, ACC_PUBLIC + ACC_STATIC);
		visitor.visitMethodInsn(INVOKESTATIC, stringValueOf.owner(), stringValueOf.methodName, stringValueOf.descriptor(), false);
	}

	/**
	 * Store the value that is at the top of the stack in the target variable
	 * 
	 * @param type   the type of the data on top of the stack
	 * @param target variable in which data will be stored
	 * @param lmv    any MethodVisitor
	 */
	public static void store(ToName type, Variable target, MethodVisitor lmv) throws Exception {
		if(!target.mutable && target.isInit()) {
			throw new IllegalArgumentException(target + " is not mutable and has been set.");
		} else if(!type.toInternalName().compatibleWith(target.type))
			throw new IncompatibleTypeException(type + INCOMPATIBLE + target);
		else {
			if(type.isBaseType()) {
				// convert integer to long
				if(target.toBaseType() == BaseType.LONG && type.toBaseType() == BaseType.INT)
					lmv.visitInsn(I2L);
				// convert float to double
				if(target.toBaseType) == BaseType.DOUBLE && type.toBaseType() == BaseType.FLOAT)
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

				target.init();
			} else
				lmv.visitVarInsn(ASTORE, taget.localIndex);
		}
	}

	public static void storeDefault(Variable lv, MethodVisitor lmv) {
		if(lv.type.isBaseType()) {
			switch (lv.type.toBaseType()) {
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
		} else {
			lmv.visitInsn(ACONST_NULL);
			lmv.visitVarInsn(ASTORE, lv.localIndex);
		}
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
				setClassName(pkgName.substring(0, pkgName.lastIndexOf(".") + 1), ctx.CLASSNAME(0).getText());
			else
				setClassName(pkgName, ctx.CLASSNAME(0).getText());
			ExternalMethodRouter.writeMethods(this, ctx.start.getLine());
			addDefaultConstructor(cw);
		} else if(getPass() == 2) {
			JavaMethodDef staticInit = JavaMethodDef.STATIC_INIT.withOwner(clazz);
			addMethodDef(staticInit);
			Actor actor = new Actor(defineMethod(staticInit), this);

			for (String name : constantNames) {
				try {
					final Literal<?> lit = Literal.parseLiteral(constantContexts.get(name).literal(), actor);
					addConstant(new Constant(name, lit.value));
				} catch (Exception e) {
					printException(e);
				}
			}
			getCurrentScope().end(ctx.stop.getLine(), actor, staticInit.returnValue);
		}
	}

	@Override
	public void enterConstant(final kdl.ConstantContext ctx) {
		// collect names
		if(getPass() == 1) {
			final String name = ctx.CONSTNAME().toString();
			if(!constantNames.contains(name)) {
				constantNames.add(name);
				constantContexts.put(name, ctx);
			}
			else
				throw new IllegalArgumentExeption("The const named " + name + " was already declared");
		}
	}

	private void consumeMethodCallStatement(kdl.MethodCallStatementContext ctx, Actor actor) throws Exception {
		kdl.MethodCallContext mcc = ctx.methodCall();
		if(mcc.VARNAME().size() > 1)
			getLocalVariable(mcc.VARNAME(0).getText()).push(actor);
		new MethodCall(ctx, actor).push(actor);
	}

	private void consumeNewObject(kdl.NewObjectContext ctx, Actor actor) throws Exception {
		new NewObject(ctx, actor).push(actor);
	}

	private void consumeVariableDeclaration(kdl.VariableDeclarationContext ctx, Actor actor) throws Exception {
		new VariableDeclaration(ctx, actor).push(actor);
	}

	private void consumeVariableAssignment(kdl.VariableAssignmentContext ctx, Actor actor) throws Exception {
		Variable target = getLocalVariable(ctx.VARNAME().getText());
		final ToName resultType;
		if(ctx.assignment().operatorAssign() != null)
			resultType = ExpressionHandler.compute(new Expression(getLocalVariable(ctx.VARNAME().getText()), Pushable.parse(actor, ctx.assignment().operatorAssign().value()),
					Operator.match(ctx.assignment().operatorAssign().operator().getText())), actor);
		else
			resultType = new VariableAssignment(new Expression(ctx.assignment().expression(), actor), target).push(actor);
		store(resultType, target, actor);
	}

	public void consumeStatement(final kdl.StatementContext ctx, Actor actor) throws Exception {
		if(ctx.variableDeclaration() != null) {
			consumeVariableDeclaration(ctx.variableDeclaration(), actor);
		} else if(ctx.variableAssignment() != null) {
			consumeVariableAssignment(ctx.variableAssignment(), actor);
		} else if(ctx.methodCallStatement() != null) {
			consumeMethodCallStatement(ctx.methodCallStatement(), actor);
		} else if(ctx.conditional() != null) {
			// forward to the handler to partition code
			cmpHandler.handle(ctx.conditional(), actor);
		} else if(ctx.returnStatement() != null) {
			if(ctx.returnStatement().expression() == null) {
				actor.visitInsn(RETURN);
				return;
			}

			ToName returnType = ExpressionHandler.compute(new Expression(ctx.returnStatement().expression(), actor), actor);
			if(returnType.isBaseType() && !returnType.toInternalName().isArray() {
				switch (returnType.toBaseType()) {
					case BOOLEAN:
					case INT:
						actor.visitInsn(IRETURN);
						break;
					case STRING:
						actor.visitInsn(ARETURN);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			} else
				actor.visitInsn(ARETURN);
		} else if(ctx.newObject() != null) {
			consumeNewObject(ctx.newObject(), actor);
		} else
			throw new UnimplementedException("A type of statement couldn't be interpreted " + ctx.getText());
	}

	public void consumeBlock(final kdl.BlockContext ctx, Actor actor) throws Excption {
		for (kdl.StatementContext statement : ctx.statement())
			consumeStatement(statement, actor);
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
			addImport(jvmClas);
		} catch (Exception e) {
			printException(e);
		}
	}

	public void addImport(Class<?> clazz) {
		imports.add(new InternalName(lazz));
		for (Method method : clazz.getMethods()) {
			methods.add(new JavaMethodDef(clazz, method));
		}
	}

	@Override
	public void enterMethodDefinition(final kdl.MethodDefinitionContext ctx) {
		try {
			// parse name and return type
			final Details details;
			if(ctx.methodHeader().typedVariable() != null)
				details = parseTypedVariable(ctx.methodHeader().typedVariable());
			else
				details = new Details(ctx.methodHeader().VARNAME().getText(), null, false);
			final ReturnValue rv = new ReturnValue(details.type);

			// parse parameters
			final BestList<Details> params = new BestLit<>();
			for (kdl.TypedVariableContext typedVar : ctx.methodHeader().parameterDefinition().typedVariable())
				params.add(parseTypedVariable(typedVar));

			// create MethodDef
			final BestList<InternalName> paramTypes = new BestLit<>();
			for (Details param : params)
				paramTypes.add(param.type);
			MethodDef def = new MethodDef(new InternalName(clazz), MethodDef.Type.FNC, details.name, paramTypes, rv, ACC_PUBLIC + ACC_STATIC);

			if(getPass() == 2) {
				addMethodDef(def);
			} else if(getPass() == 3) {
				final Actor actor = new Actor(defineMethod(def), his);
				for (Details param : params)
					getCurrentScope().newVariable(param.name, param.type);
				consumeBlock(ctx.block(), actor);

				getCurrentScope().end(ctx.stop.getLine(), actor, rv);
		}
		} catch (Exception e) {
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
		} else if(getPass() == 3) {
			final Actor actor = new Actor(defineMethod(JavaMethodDef.MAIN), this);
			getCurrentScope().newVariable("args", new InternalName(String.class, 1));
			try {
				consumeBlock(ctx.block(), acto);
			} catch (Exception e) {
				printException(e);
			}
			getCurrentScope().end(ctx.stop.getLine(), actor, ReturnValue.VOID);
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

	public boolean hasConstant(final Stringname) {
		for (Constant<?> c : constants) {
			if(c.name.equals(name))
				return true;
		}
		return false;
	}

	public Constant<?> getConstant(final Stringname) {
		for (Constant<?> c : constants) {
			if(c.name.equals(name))
				return c;
		}
		throw new IllegalArgumentException("Constant " + name + " does not exist");
	}

	public boolean isConstantSet() {
		for (Constant<?> c : constants)
			if(!c.isEvaluated())
				return false;
		return true;
	}

	/**
	 * Sets the name of this class to the given className.
	 * 
	 * @param name name of class, Ex. Test
	 * @return success of operation
	 */
	public boolean setClassName(final String pkg, final String name) {
		if(!nameSet) {
			this.clazz = new CustomClass(pkg, name);
			nameSet = true;

			// give name to ClassWriter
			cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, clazz.toInternalName().nameString(), null, InternalName.OBJECT.nameString(), null);
			cw.visitSource(clazz + ".kdl", null);

			return true;
		} else
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
		} else
			throw new IllegalArgumentException("None of the detected method definitions match the given method definition");
	}

	public FieldVisitor addConstant(final Constant<?> c) {
		if(constants.contains(c))
			throw new IllegalArgumentException("The constant with name " + c.name + " has already been defined.");
		constants.add(c);
		FieldVisitor fv;
		BaseType bt;
		if((bt = BaseType.matchValue(c.value)) != null) {
			fv = cw.visitField(CONST_ACCESS, c.name, bt.toInternalName().objectString(), null, c.value);
			fv.visitEnd();
		} else {
			fv = cw.visitField(CONST_ACCESS, c.name, new InternalName(c.value.getClass()).objectString(), null, null);
			fv.visitEnd();
		}
		return fv;
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
		mv.visitLocalVariable("this", clazz.toInternalName().objectString(), null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

	public Scope getCurrentScope() {
		return currentScope;
	}

}
