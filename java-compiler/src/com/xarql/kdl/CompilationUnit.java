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

import com.xarql.kdl.ir.*;
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
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import com.xarql.kdl.names.ToName;

public class CompilationUnit extends kdlBaseListener implements Runnable, CommonText {

	public static final int CONST_ACCESS = ACC_PUBLIC + ACC_STATIC + ACC_FINAL;
	public static final String INCORRECT_FILE_NAME = "The input file name must match its class name.";

	// used to generate a numerical id
	private static int unitCount = 0;

	// input and output
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

	public final TrackedMap<Constant, kdl.ConstantDefContext> constants;
	public final TrackedMap<Field, kdl.FieldDefContext> fields;

	private String pkgName;

	// pass 1 collects imports, classname, and constant names, methodNames
	// pass 2 assigns values to constants
	// pass 3 defines methods
	private int pass;

	private CompilationUnit() {
		pass = 0;
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		constants = new TrackedMap<>();
		imports = new BestList<>();
		methods = new BestList<>();
		fields = new TrackedMap<>();
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
			compile();
			if(outputFile != null)
				write(outputFile);
			else
				write();
			System.out.println("Compiled " + clazz.name);
		} catch(Exception e) {
			System.err.println("CompilationUnit " + unitName() + " aborted.");
			e.printStackTrace();
		}
	}

	public CompilationUnit compile() throws Exception {
		final ParseTree tree = makeParseTree(sourceCode);
		newPass();
		ParseTreeWalker.DEFAULT.walk(this, tree);
		addImport(getClazz().toInternalName());
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
				throw new IllegalArgumentException("Couldn't recognize type");
		}

		if(ctx.type().BRACE_OPEN() != null)

		{
			int dimensions = 0;
			for(int i = 0; i < ctx.type().BRACE_OPEN().size(); i++)
				dimensions++;
			type = type.toArray(dimensions);
		}

		return new Details(name, type, ctx.MUTABLE() != null);
	}

	public InternalName resolveAgainstImports(String src) {
		for(InternalName in : imports) {
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
				if(target.toBaseType() == BaseType.DOUBLE && type.toBaseType() == BaseType.FLOAT)
					lmv.visitInsn(F2D);
			}

			if(target.isBaseType()) {
				switch(target.toBaseType()) {
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
				setClassName(pkgName.substring(0, pkgName.lastIndexOf(".") + 1), ctx.CLASSNAME().getText());
			else
				setClassName(pkgName, ctx.CLASSNAME().getText());
			ExternalMethodRouter.writeMethods(this, ctx.start.getLine());
		} else if(getPass() == 2) {
			if(!constants.isEmpty()) {
				JavaMethodDef staticInit = JavaMethodDef.STATIC_INIT.withOwner(clazz);
				addMethodDef(staticInit);
				Actor actor = new Actor(defineMethod(staticInit), this);

				for(kdl.ConstantDefContext c : constants) {
					try {
						final Pushable pushable = Literal.parseLiteral(c.literal(), actor);
						final Constant unsetConst = new Constant(c.CONSTNAME().getText(), pushable.toInternalName(), clazz.toInternalName());
						addConstant(unsetConst);
						constants.put(unsetConst, c);
						pushable.push(actor);
						actor.visitFieldInsn(PUTSTATIC, unsetConst.owner.nameString(), unsetConst.name, unsetConst.type.objectString());
					} catch(Exception e) {
						printException(e);
					}
				}
				getCurrentScope().end(ctx.stop.getLine(), actor, staticInit.returnValue);
			}

			for(Field f : fields.keys()) {
				final FieldVisitor fv;
				final Object defaultValue;
				if(f.type.toBaseType() == STRING)
					defaultValue = "placeholder";
				else if(f.type.isBaseType())
					defaultValue = f.toBaseType().defaultValue.value;
				else
					defaultValue = null;
				fv = cw.visitField(ACC_PUBLIC + ACC_FINAL, f.name, f.type.objectString(), null, defaultValue);
				fv.visitEnd();
			}

			try {
				addDefaultConstructor(cw);
			} catch(Exception e) {
				printException(e);
			}
		}
	}

	@Override
	public void enterFieldDef(kdl.FieldDefContext ctx) {
		// collect details
		if(getPass() == 1) {
			try {
				final Details details = parseTypedVariable(ctx.variableDeclaration().typedVariable());
				final Field field = new Field(details, getClazz());
				if(!fields.contains(field))
					fields.put(field, ctx);
				else
					throw new IllegalArgumentException("The field named " + details.name + " was already declared within " + getClazz());
			} catch(Exception e) {
				throw new IllegalArgumentException(
						"Couldn't determine the name, type and mutability of a field at " + ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine());
			}
		}
	}

	@Override
	public void enterConstantDef(final kdl.ConstantDefContext ctx) {
		// collect details
		if(getPass() == 1) {
			final String name = ctx.CONSTNAME().toString();
			final Constant unsetConst = new Constant(name, InternalName.INT, getClazz().toInternalName());
			if(!constants.contains(unsetConst))
				constants.put(unsetConst, ctx);
			else
				throw new IllegalArgumentException("The const named " + name + " was already declared within " + getClazz());
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

	private void consumeAssignment(kdl.AssignmentContext ctx, Actor actor) throws Exception {
		if(ctx.VARNAME() != null) {
			final Variable target = getLocalVariable(ctx.VARNAME().getText());
			final ToName resultType;
			if (ctx.operatorAssign() != null)
				resultType = new Expression(target, Pushable.parse(actor, ctx.operatorAssign().value()), Operator.match(ctx.operatorAssign().operator().getText())).push(actor);
			else
				resultType = new VariableAssignment(new Expression(ctx.expression(), actor), target).push(actor);
			store(resultType, target, actor);
		} else {
			final Field field = fields.equivalentKey(new Field(new Details(ctx.field().VARNAME(0).getText(), null, false), getClazz()));
			if(ctx.operatorAssign() != null)
				new Expression(field, Pushable.parse(actor, ctx.operatorAssign().value()), Operator.match(ctx.operatorAssign().operator().getText())).push(actor);
			else
				new Expression(ctx.expression(), actor).push(actor);
			field.store(actor);
		}
	}

	public void consumeStatement(final kdl.StatementContext ctx, Actor actor) throws Exception {
		if(ctx.variableDeclaration() != null) {
			consumeVariableDeclaration(ctx.variableDeclaration(), actor);
		} else if(ctx.assignment() != null) {
			consumeAssignment(ctx.assignment(), actor);
		} else if(ctx.methodCallStatement() != null) {
			consumeMethodCallStatement(ctx.methodCallStatement(), actor);
		} else if(ctx.conditional() != null) {
			// forward to the handler to partition code
			ConditionalHandler.handle(ctx.conditional(), actor);
		} else if(ctx.returnStatement() != null) {
			if(ctx.returnStatement().expression() == null) {
				actor.visitInsn(RETURN);
				return;
			}

			ToName returnType = ExpressionHandler.compute(new Expression(ctx.returnStatement().expression(), actor), actor);
			if(returnType.isBaseType() && !returnType.toInternalName().isArray()) {
				switch(returnType.toBaseType()) {
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

	public void consumeBlock(final kdl.BlockContext ctx, Actor actor) throws Exception {
		for(kdl.StatementContext statement : ctx.statement())
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

	public void addImport(InternalName internalName) {
		imports.add(internalName);
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
			final BestList<Details> params = new BestList<>();
			for(kdl.TypedVariableContext typedVar : ctx.methodHeader().parameterDefinition().typedVariable())
				params.add(parseTypedVariable(typedVar));

			// create MethodDef
			final BestList<InternalName> paramTypes = new BestList<>();
			for(Details param : params)
				paramTypes.add(param.type);

			// check if the method accesses any fields
			final int staticModifier;
			if(ctx.methodHeader().TYPE() == null)
				staticModifier = ACC_STATIC;
			else
				staticModifier = 0;

			MethodDef def = new MethodDef(new InternalName(clazz), MethodDef.Type.FNC, details.name, paramTypes, rv, ACC_PUBLIC + staticModifier);

			if(getPass() == 2) {
				addMethodDef(def);
			} else if(getPass() == 3) {
				final Actor actor = new Actor(defineMethod(def), this);
				// instance of owning type will always occupy slot 0
				if(staticModifier != 0)
					getCurrentScope().newVariable("this", getClazz().toInternalName());
				// parameters will always occupy the first few slots
				for(Details param : params)
					getCurrentScope().newVariable(param.name, param.type);
				consumeBlock(ctx.block(), actor);

				getCurrentScope().end(ctx.stop.getLine(), actor, rv);
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
		} else if(getPass() == 3) {
			final Actor actor = new Actor(defineMethod(JavaMethodDef.MAIN.withOwner(clazz)), this);
			getCurrentScope().newVariable("args", new InternalName(String.class, 1));
			try {
				consumeBlock(ctx.block(), actor);
			} catch(Exception e) {
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

	public boolean hasConstant(final String name) {
		for(Constant c : constants.keys()) {
			if(c.name.equals(name))
				return true;
		}
		return false;
	}

	public Constant getConstant(final String name) {
		for(Constant c : constants.keys()) {
			if(c.name.equals(name))
				return c;
		}
		throw new IllegalArgumentException("Constant " + name + " does not exist");
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

	public FieldVisitor addConstant(final Constant c) {
		final FieldVisitor fv;
		final Object defaultValue;
		if(c.toBaseType() == STRING)
			defaultValue = "placeholder";
		else if(c.isBaseType())
			defaultValue = c.toBaseType().defaultValue;
		else
			defaultValue = null;
		fv = cw.visitField(CONST_ACCESS, c.name, c.type.objectString(), null, defaultValue);
		fv.visitEnd();
		return fv;
	}

	public void addDefaultConstructor(final ClassWriter cw) throws Exception {
		addMethodDef(JavaMethodDef.INIT.withOwner(getClazz()));
		final Actor actor = new Actor(defineMethod(JavaMethodDef.INIT.withOwner(getClazz())), this);
		actor.visitCode();
		final Label start = new Label();
		actor.visitLabel(start);
		actor.visitLineNumber(0, start);

		// call Object.super(this);
		actor.visitVarInsn(ALOAD, 0);
		actor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

		for(Field f : fields.keys()) {
			// push "this"
			actor.visitVarInsn(ALOAD, 0);
			if(fields.get(f).variableDeclaration().ASSIGN() != null) {
				final Expression xpr = new Expression(fields.get(f).variableDeclaration().expression(), actor);
				xpr.push(actor);
			} else {
				if(f.type.isBaseType())
					f.type.toBaseType().defaultValue.push(actor);
				else
					actor.visitInsn(ACONST_NULL);
			}
			actor.visitFieldInsn(PUTFIELD, getClazz().toInternalName().nameString(), f.name, f.type.objectString());
		}
		final Label finish = new Label();
		actor.visitLabel(finish);
		actor.visitInsn(RETURN);
		actor.visitLocalVariable("this", clazz.toInternalName().objectString(), null, start, finish, 0);
		actor.visitMaxs(0, 0);
		actor.visitEnd();
	}

	public Scope getCurrentScope() {
		return currentScope;
	}

}
