package com.xarql.kdl;

import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.antlr.kdlBaseListener;
import com.xarql.kdl.antlr.kdlLexer;
import com.xarql.kdl.ir.*;
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
import java.util.HashSet;
import java.util.Set;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.Text.nonNull;
import static com.xarql.kdl.names.BaseType.*;

public class CompilationUnit extends kdlBaseListener implements Runnable, CommonText {

	public static final int CONST_ACCESS = ACC_PUBLIC + ACC_STATIC + ACC_FINAL;
	public static final String INCORRECT_FILE_NAME = "The input file name must match its class name.";

	// used to generate a numerical id
	private static int unitCount = 0;
	public final TrackedMap<Constant, kdl.ConstantDefContext> constants;
	public final TrackedMap<StaticField, kdl.FieldDefContext> fields;
	// set in constructor
	private final ClassWriter cw;
	private final Set<InternalName> imports;
	private final Set<MethodHeader> methods;
	private final int id;
	// input and output
	private File sourceFile;
	private String sourceCode;
	private File outputFile;
	private Scope currentScope;
	private CustomClass clazz;
	private boolean nameSet;
	private String pkgName;

	// pass 1 collects imports, classname, and constant names, methodNames
	// pass 2 assigns values to constants
	// pass 3 defines methods
	private int pass;

	private CompilationUnit() {
		pass = 0;
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		constants = new TrackedMap<>();
		imports = new HashSet<>();
		methods = new HashSet<>();
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

	/**
	 * Converts the top item of the stack in to a string
	 *
	 * @param name    The type of the element on the stack
	 * @param visitor Any MethodVisitor
	 */
	public static void convertToString(final InternalName name, final MethodVisitor visitor) {
		MethodHeader stringValueOf;
		if(name.isBaseType()) {
			if(name.toBaseType() == STRING)
				return;
			else {
				InternalName actualName = name;
				if(name.toBaseType() == BYTE)
					actualName = InternalName.INT;
				else if(name.toBaseType() == SHORT)
					actualName = InternalName.INT;
				stringValueOf = new MethodHeader(InternalName.STRING, "valueOf", MethodHeader.toParamList(actualName), ReturnValue.STRING, ACC_PUBLIC + ACC_STATIC);
			}
		} else
			stringValueOf = new MethodHeader(InternalName.STRING, "valueOf", MethodHeader.toParamList(InternalName.OBJECT), ReturnValue.STRING, ACC_PUBLIC + ACC_STATIC);
		visitor.visitMethodInsn(INVOKESTATIC, stringValueOf.owner(), stringValueOf.name, stringValueOf.descriptor(), false);
	}

	public void runSilent() throws Exception {
		run0();
	}

	@Override
	public void run() {
		try {
			System.out.println("Compiled " + run0());
		} catch(Exception e) {
			System.err.println("CompilationUnit " + unitName() + " aborted.");
			e.printStackTrace();
		}
	}

	private String run0() throws Exception {
		// load source code
		if(sourceCode == null)
			sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
		compile();
		if(outputFile != null)
			write(outputFile);
		else
			write();
		return clazz.name;
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

	public InternalName resolveAgainstImports(String classname) {
		for(InternalName in : imports) {
			String str = in.nameString();
			if(str.contains("/") && str.lastIndexOf("/") + 1 <= str.length() && str.substring(str.lastIndexOf("/") + 1).equals(classname))
				return in;
		}
		throw new IllegalArgumentException("Couldn't recognize type: " + classname);
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
				MethodHeader staticInit = MethodHeader.STATIC_INIT.withOwner(clazz);
				addMethodDef(staticInit);
				Actor actor = new Actor(defineMethod(staticInit), this);

				for(Constant c : constants.keys()) {
					try {
						final kdl.ConstantDefContext cDef = constants.get(c);
						final Pushable pushable = Pushable.parse(actor, cDef.value());
						final Constant unsetConst = new Constant(c.name, pushable.toInternalName(), clazz.toInternalName());
						addConstant(unsetConst);
						constants.put(unsetConst, cDef);
						pushable.push(actor);
						actor.visitFieldInsn(PUTSTATIC, unsetConst.owner.nameString(), unsetConst.name, pushable.toInternalName().objectString());
					} catch(Exception e) {
						printException(e);
					}
				}
				getCurrentScope().end(ctx.stop.getLine(), actor, staticInit.returns);
			}

			for(StaticField f : fields.keys()) {
				if(f instanceof ObjectField) {
					final FieldVisitor fv;
					final Object defaultValue;
					if(f.type.toBaseType() == STRING)
						defaultValue = "placeholder";
					else if(f.type.isBaseType())
						defaultValue = f.toBaseType().defaultValue.value;
					else
						defaultValue = null;
					int modifier = 0;
					if(f.mutable)
						modifier = ACC_FINAL;
					fv = cw.visitField(ACC_PUBLIC + modifier, f.name, f.type.objectString(), null, defaultValue);
					fv.visitEnd();
				}
			}

			try {
				addDefaultConstructor();
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
				final Details details = new Details(ctx.variableDeclaration().details(), this);
				final ObjectField field = new ObjectField(details, getClazz());
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
			final Constant unsetConst = new Constant(name, InternalName.PLACEHOLDER, getClazz().toInternalName());
			if(!constants.contains(unsetConst))
				constants.put(unsetConst, ctx);
			else
				throw new IllegalArgumentException("The const named " + name + " was already declared within " + getClazz());
		}
	}

	private void consumeMethodCallStatement(kdl.MethodCallStatementContext ctx, Actor actor) throws Exception {
		new MethodCall(ctx, actor).push(actor);
	}

	private void consumeNewObject(kdl.NewObjectContext ctx, Actor actor) throws Exception {
		new NewObject(ctx, actor).push(actor);
	}

	private void consumeAssignment(kdl.AssignmentContext ctx, Actor actor) throws Exception {
		final Assignable target = Assignable.parse(ctx, actor);
		final InternalName resultType;
		if(ctx.operatorAssign() != null)
			resultType = new Expression(target, Pushable.parse(actor, ctx.operatorAssign().value()), Operator.match(ctx.operatorAssign().operator().getText())).pushType(actor);
		else
			resultType = new Expression(ctx.expression(), actor).pushType(actor);
		target.assign(resultType, actor);
	}

	private void consumeVariableDeclaration(kdl.VariableDeclarationContext ctx, Actor actor) throws Exception {
		final Variable target = getCurrentScope().newVariable(new Details(ctx.details(), this));
		if(ctx.ASSIGN() != null) {
			target.assign(new Expression(ctx.expression(), actor).pushType(actor), actor);
		} else
			target.assignDefault(actor);
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
			final ReturnValue rv;
			if(ctx.returnStatement().expression() == null)
				rv = null;
			else
				rv = new ReturnValue(ExpressionHandler.compute(new Expression(ctx.returnStatement().expression(), actor), actor));
			actor.writeReturn(rv);
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
			addImport(ctx.getText().substring(3));
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
			methods.add(new MethodHeader(clazz, method));
		}
		for(java.lang.reflect.Field field : clazz.getFields()) {
			final Details details = new Details(field.getName(), new InternalName(field.getType()), (field.getModifiers() & ACC_FINAL) == ACC_FINAL);
			if((field.getModifiers() & ACC_STATIC) == ACC_STATIC) {
				fields.add(new StaticField(details, new InternalName(clazz)), null);
			} else {
				fields.add(new ObjectField(details, new InternalName(clazz)), null);
			}
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
			if(ctx.details() != null)
				details = new Details(ctx.details(), this).filterName();
			else
				details = new Details(ctx.VARNAME().getText(), null).filterName();
			final ReturnValue rv = new ReturnValue(details.type);

			// parse parameters
			final BestList<Param> params = new BestList<>();
			for(kdl.ParamContext param : ctx.paramSet().param())
				params.add(new Param(new Details(param.details(), this), param.value()));

			// check if the method accesses any fields
			final boolean initializer;
			final int staticModifier;
			if(details.name.equals(MethodHeader.S_INIT)) {
				staticModifier = 0;
				initializer = true;
			} else {
				if(ctx.paramSet().VARNAME() == null)
					staticModifier = ACC_STATIC;
				else {
					if(ctx.paramSet().VARNAME().getText().equals("this"))
						staticModifier = 0;
					else
						throw new IllegalArgumentException("Only \"this\" may be used as a non typed argument");
				}
				initializer = false;
			}

			MethodHeader def = new MethodHeader(clazz.toInternalName(), details.name, params, rv, ACC_PUBLIC + staticModifier);

			if(getPass() == 2) {
				addMethodDef(def);
			} else if(getPass() == 3) {
				// define user specified method
				final Actor actor = new Actor(defineMethod(def), this);
				// instance of owning type will always occupy slot 0
				if(staticModifier == 0)
					getCurrentScope().newVariable("this", getClazz().toInternalName());
				// parameters will always occupy the first few slots
				for(Param param : params)
					getCurrentScope().newVariable(param.name, param.type);

				if(initializer) {
					// call Object.super(this);
					actor.visitVarInsn(ALOAD, 0);
					actor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
				}

				consumeBlock(ctx.block(), actor);

				getCurrentScope().end(ctx.stop.getLine(), actor, rv);

				// add in helper methods for default values
				for(Param param : params) {
					if(param.defaultValue != null) {
						final ReturnValue returnValue = new ReturnValue(param.toInternalName());
						final MethodHeader defaultProvider = new MethodHeader(clazz.toInternalName(), details.name + "_" + param.name, null, returnValue, def.access + Opcodes.ACC_SYNTHETIC);
						addMethodDef(defaultProvider);
						final Actor defaultWriter = new Actor(defineMethod(defaultProvider), this);
						Pushable.parse(actor, param.defaultValue).push(defaultWriter);
						defaultWriter.writeReturn(returnValue);
						getCurrentScope().end(ctx.start.getLine(), defaultWriter, returnValue);
					}
				}

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
			addMethodDef(MethodHeader.MAIN.withOwner(clazz));
		} else if(getPass() == 3) {
			final Actor actor = new Actor(defineMethod(MethodHeader.MAIN.withOwner(clazz)), this);
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

	public MethodHeader addMethodDef(MethodHeader md) {
		if(!methods.add(md))
			throw new IllegalArgumentException("The method " + md + " already exists in " + unitName());
		return md;
	}

	public Set<MethodHeader> getMethods() {
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

	public MethodVisitor defineMethod(MethodHeader md) {
		for(MethodHeader def : methods) {
			if(def.equals(md)) {
				final MethodVisitor mv = cw.visitMethod(md.access, md.name, md.descriptor(), null, null);
				currentScope = new Scope("Method " + md.name + " of class " + clazz, mv);
				mv.visitCode();
				return mv;
			}
		}
		throw new IllegalArgumentException("None of the detected method definitions match the given method definition");
	}

	public FieldVisitor addConstant(final Constant c) {
		final FieldVisitor fv;
		final Object defaultValue;
		if(c.toBaseType() == STRING)
			defaultValue = "placeholder";
		else if(c.isBaseType())
			defaultValue = c.toBaseType().defaultValue.value;
		else
			defaultValue = null;
		fv = cw.visitField(CONST_ACCESS, c.name, c.type.objectString(), null, defaultValue);
		fv.visitEnd();
		return fv;
	}

	public void addDefaultConstructor() throws Exception {
		addMethodDef(MethodHeader.INIT.withOwner(getClazz()));
		final Actor actor = new Actor(defineMethod(MethodHeader.INIT.withOwner(getClazz())), this);
		actor.visitCode();
		final Label start = new Label();
		actor.visitLabel(start);
		actor.visitLineNumber(0, start);

		// call Object.super(this);
		actor.visitVarInsn(ALOAD, 0);
		actor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

		for(StaticField f : fields.keys()) {
			if(f instanceof ObjectField) {
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
