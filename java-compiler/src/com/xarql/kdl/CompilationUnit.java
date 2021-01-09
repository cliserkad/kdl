package com.xarql.kdl;

import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.antlr.kdlBaseListener;
import com.xarql.kdl.antlr.kdlLexer;
import com.xarql.kdl.ir.*;
import com.xarql.kdl.names.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.xarql.kdl.Type.PATH_SEPARATOR;
import static com.xarql.kdl.names.BaseType.*;

public class CompilationUnit extends kdlBaseListener implements Runnable, CommonText {
	public static final char JAVA_SOURCE_SEPARATOR = '.';
	public static final int CONST_ACCESS = ACC_PUBLIC + ACC_STATIC + ACC_FINAL;
	public static final String INCORRECT_FILE_NAME = "The input file name must match its class name.";
	public static final int PASSES = 3;

	// used to generate a numerical id
	private static int unitCount = 0;

	public final int id;
	public final CompilationDispatcher owner;
	public Path path;
	public Type type;
	public Set<Type> imports;

	// input and output
	public ClassWriter cw;
	private final List<String> warnings;
	private File sourceFile;
	private String sourceCode;
	private File outputDir;
	private ParseTree tree;

	// pass 1 collects imports, classname, and constant names, methodNames
	// pass 2 assigns values to constants
	// pass 3 defines methods
	private int pass;

	private CompilationUnit(CompilationDispatcher owner) {
		this.owner = owner;
		pass = 0;
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS + ClassWriter.COMPUTE_FRAMES);
		id = unitCount++;
		type = null;
		path = new Path();
		imports = new HashSet<>();
		addImport(String.class);
		tree = null;
		warnings = new BestList<>("KDL is an unfinished language and may produce broken class files");
	}

	public CompilationUnit(CompilationDispatcher owner, File sourceFile, File outputDir) {
		this(owner);
		this.sourceFile = sourceFile;
		this.outputDir = outputDir;
	}

	public CompilationUnit(CompilationDispatcher owner, File sourceFile) {
		this(owner, sourceFile, null);
	}

	public CompilationUnit(CompilationDispatcher owner, String sourceCode) {
		this(owner);
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

	public boolean pass() throws Exception {
		if(sourceCode == null)
			sourceCode = new String(Files.readAllBytes(sourceFile.toPath()));
		if(tree == null)
			tree = makeParseTree(sourceCode);

		newPass();
		ParseTreeWalker.DEFAULT.walk((ParseTreeListener) this, tree);

		if(pass == 1)
			addImport(getType());

		return pass >= PASSES;
	}

	public void warn(String msg) {
		warnings.add(msg);
	}

	@Override
	public void run() {
		try {
			pass();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isFromFile() {
		return sourceFile != null;
	}

	public java.nio.file.Path sourcePath() {
		return sourceFile.toPath();
	}

	public String unitName() {
		if(type != null)
			return type.toInternalName().qualifiedName();
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
			throw new Exception();
		}

		return tree;
	}

	public File write(File destination) throws IOException {
		cw.visitEnd();
		if(destination == null)
			write();

		// check input file name
		if(sourceFile != null && !sourceFile.getName().replace(".kdl", "").equalsIgnoreCase(type.name.name()))
			throw new IllegalArgumentException(INCORRECT_FILE_NAME + " file:" + sourceFile.getName() + " class:" + type.name);

		destination = new File(destination, type.toInternalName().qualifiedName() + ".class");
		destination.getParentFile().mkdirs();
		destination.createNewFile();

		Files.write(destination.toPath(), cw.toByteArray());
		return destination;
	}

	public File write() throws IOException {
		if(sourceFile == null)
			throw new NullPointerException("write() without params in CompilationUnit if the unit wasn't created with a file.");
		return write(outputDir);
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
			if(setClassName(ctx.IDENTIFIER().getText())) {}
			else
				System.err.println("Type was already named:" + type);
			owner.types.add(getType());
			try {
				ExternalMethodRouter.writeMethods(this, ctx.start.getLine());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if(getPass() == 2) {
			if(!type.constants.isEmpty()) {
				MethodHeader staticInit = MethodHeader.STATIC_INIT.withOwner(type);
				Actor actor = defineMethod(staticInit);

				for(Constant c : type.constants.keys()) {
					if(c.owner.equals(getType().toInternalName())) {
						try {
							final kdl.ReservationContext cDef = type.constants.get(c);
							final Expression expression = new Expression(type, cDef.expression(), actor);
							final Constant unsetConst = new Constant(c.name.text, expression.toInternalName(), type.toInternalName());
							addConstant(unsetConst);
							type.constants.put(unsetConst, cDef);
							expression.push(actor);
							actor.visitFieldInsn(PUTSTATIC, unsetConst.owner.qualifiedName(), unsetConst.name.text, expression.toInternalName().arrayName());
						} catch (Exception e) {
							printException(e);
						}
					}
				}
				actor.scope.end(ctx.stop.getLine(), actor, staticInit.returns);
			}

			for(StaticField f : type.fields.keys()) {
				if(f.ownerType.equals(getType().toInternalName()) && f instanceof ObjectField) {
					final FieldVisitor fv;
					int modifier = 0;
					if(f.mutable)
						modifier = ACC_FINAL;
					fv = cw.visitField(ACC_PUBLIC + modifier, f.name.text, f.type.arrayName(), null, f.type.defaultValue());
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
	public void enterReservation(kdl.ReservationContext ctx) {
		if(getPass() == 1 && ctx.parent.getRuleIndex() == kdl.RULE_clazz) {
			try {
				final Details details = new Details(ctx.details(), this);
				if(details.constant) {
					final Constant unsetConst = new Constant(details.name.text, InternalName.PLACEHOLDER, getType().toInternalName());
					if(!type.constants.contains(unsetConst))
						type.constants.put(unsetConst, ctx);
					else
						throw new IllegalArgumentException(unsetConst + " was already declared");
				} else {
					final ObjectField field = new ObjectField(details, getType().toInternalName());
					if (!type.fields.contains(field))
						type.fields.put(field, ctx);
					else
						throw new IllegalArgumentException("The field named " + details.name + " was already declared within " + getType());
				}
			} catch(Exception e) {
				e.printStackTrace();
				throw new IllegalArgumentException("Couldn't determine the name, type and mutability of a field at " + ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine());
			}
		}
	}

	private void consumeAssignment(kdl.AssignmentContext ctx, Actor actor) throws Exception {
		final Assignable target = Assignable.parse(ctx, actor);
		final InternalName resultType;
		if(ctx.operator() != null)
			resultType = new Expression(target, new Expression(type, ctx.expression().get(1), actor), Operator.match(ctx.operator().getText())).push(actor).toInternalName();
		else
			resultType = new Expression(type, ctx.expression().get(1), actor).push(actor).toInternalName();
		target.assign(resultType, actor);
	}

	private void consumeVariableDeclaration(kdl.ReservationContext ctx, Actor actor) throws Exception {
		final Variable target = actor.scope.newVar(new Details(ctx.details(), this));
		if(ctx.COLON() != null) {
			target.assign(new Expression(type, ctx.expression(), actor).push(actor).toInternalName(), actor);
		} else
			target.assignDefault(actor);
	}

	public void consumePart(final kdl.StatementContext ctx, Actor actor) throws Exception {
		if(ctx.imperative() != null) {
			if(ctx.imperative().reservation() != null) {
				consumeVariableDeclaration(ctx.imperative().reservation(), actor);
			} else if(ctx.imperative().assignment() != null) {
				consumeAssignment(ctx.imperative().assignment(), actor);
			} else if(ctx.imperative().expression() != null) {
				new Expression(type, ctx.imperative().expression(), actor).push(actor);
			} else if(ctx.imperative().returnStatement() != null) {
				final ReturnValue rv;
				if(ctx.imperative().returnStatement().expression() == null)
					rv = null;
				else
					rv = new ReturnValue(new Expression(type, ctx.imperative().returnStatement().expression(), actor).push(actor));
				actor.writeReturn(rv);
			} else
				throw new UnimplementedException("A type of Imperative couldn't be interpreted " + ctx.getText());
		} else if(ctx.conditional() != null) {
			ConditionalHandler.handle(ctx.conditional(), actor);
		} else
			throw new UnimplementedException("A type of Statement couldn't be interpreted " + ctx.getText());
	}

	public void consumeBlock(final kdl.BlockContext ctx, Actor actor) throws Exception {
		for(kdl.StatementContext statement : ctx.statement()) {
			consumePart(statement, actor);
		}
	}

	@Override
	public void enterPath(final kdl.PathContext ctx) {
		if(pass == 1) {
			path = path.prepend(ctx.getText().trim().substring(4));
			type = new Type(new InternalName(path));
		}
	}

	@Override
	public void enterUse(final kdl.UseContext ctx) {
		if(pass == 1) {
			addImport(ctx.getText().substring(3));
		}
	}

	public void addImport(String text) {
		try {
			final Class<?> jvmClass = Class.forName(text.replace(PATH_SEPARATOR, JAVA_SOURCE_SEPARATOR));
			addImport(jvmClass);
		} catch(Exception e) {
			printException(e);
		}
	}

	public void addImport(Class<?> clazz) {
		addImport(new Type(clazz));
	}

	public void addImport(Type dc) {
		imports.add(dc);
	}

	@Override
	public void enterMethodDefinition(final kdl.MethodDefinitionContext ctx) {
		try {
			// parse name and return type
			final Details details;
			if(ctx.details() != null)
				details = new Details(ctx.details(), this).filterName();
			else
				details = new Details(ctx.IDENTIFIER().getText(), null).filterName();
			final ReturnValue rv = new ReturnValue(details.type);

			// parse parameters
			final BestList<Param> params = new BestList<>();
			for(kdl.ParamContext param : ctx.parameterSet().param())
				params.add(new Param(new Details(param.details(), this), param.expression()));

			// check if the method accesses any fields
			final boolean initializer;
			final int staticModifier;
			if(details.name.text.equals(MethodHeader.S_INIT)) {
				staticModifier = 0;
				initializer = true;
			} else {
				if(ctx.parameterSet().THIS() == null)
					staticModifier = ACC_STATIC;
				else
					staticModifier = 0;

				initializer = false;
			}


			MethodHeader def = new MethodHeader(type.toInternalName(), details.name.text, params, rv, ACC_PUBLIC + staticModifier);

			if(getPass() == 2) {
				registerMethod(def);
			} else if(getPass() == 3) {
				// define user specified method
				final Actor actor = defineMethod(def);
				// instance of owning type will always occupy slot 0
				if(staticModifier == 0)
					actor.scope.newVar("this", getType().toInternalName());
				// parameters will always occupy the first few slots
				for(Param param : params)
					actor.scope.newVar(param.name.text, param.type);

				if(initializer) {
					// call Object.super(this);
					actor.visitVarInsn(ALOAD, 0);
					actor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
				}

				consumeBlock(ctx.block(), actor);

				actor.scope.end(ctx.stop.getLine(), actor, rv);

				// add in helper methods for default values
				for(Param param : params) {
					if(param.defaultValue != null) {
						final ReturnValue returnValue = new ReturnValue(param.toInternalName());
						final MethodHeader defaultProvider = new MethodHeader(type.toInternalName(), details.name + "_" + param.name, null, returnValue, def.access + Opcodes.ACC_SYNTHETIC);
						final Actor defaultWriter = defineMethod(defaultProvider);
						new Expression(type, param.defaultValue, actor).push(defaultWriter);
						defaultWriter.writeReturn(returnValue);
						actor.scope.end(ctx.start.getLine(), defaultWriter, returnValue);
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
			registerMethod(MethodHeader.MAIN.withOwner(type));
		} else if(getPass() == 3) {
			final Actor actor = defineMethod(MethodHeader.MAIN.withOwner(type));
			actor.scope.newVar("args", new InternalName(String.class, 1));
			try {
				consumeBlock(ctx.block(), actor);
			} catch(Exception e) {
				printException(e);
			}
			actor.scope.end(ctx.stop.getLine(), actor, ReturnValue.VOID);
		}
	}

	public MethodHeader registerMethod(MethodHeader md) {
		if(!type.methods.add(md))
			throw new IllegalArgumentException("The method " + md + " already exists in " + unitName());
		return md;
	}

	public boolean hasConstant(final String name) {
		for(Constant c : type.constants.keys()) {
			if(c.name.text.equals(name))
				return true;
		}
		return false;
	}

	public Constant getConstant(final String name) {
		for(Constant c : type.constants.keys()) {
			if(c.name.text.equals(name))
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
	public boolean setClassName(final String name) {
		if(type == null) {
			path = path.append(name);
			type = new Type(new InternalName(path));

			// give name to ClassWriter
			cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, type.name.qualifiedName(), null, InternalName.OBJECT.qualifiedName(), null);
			cw.visitSource(type.name + ".kdl", null);

			return true;
		} else
			return false;
	}

	public Type getType() {
		return type;
	}

	public Actor defineMethod(MethodHeader target) {
		for(MethodHeader def : type.methods) {
			if(def.equals(target)) {
				return Actor.build(def, this);
			}
		}
		return Actor.build(registerMethod(target), this);
	}

	public FieldVisitor addConstant(final Constant c) {
		final FieldVisitor fv;
		final Object defaultValue;
		if(c.toBaseType() == STRING)
			defaultValue = "placeholder";
		else if(c.isBaseType())
			defaultValue = c.toBaseType().getDefaultValue().value;
		else
			defaultValue = null;
		fv = cw.visitField(CONST_ACCESS, c.name.text, c.type.arrayName(), null, defaultValue);
		fv.visitEnd();
		return fv;
	}

	public void addDefaultConstructor() throws Exception {
		final Actor actor = defineMethod(MethodHeader.INIT.withOwner(getType()));
		actor.visitCode();
		final Label start = new Label();
		actor.visitLabel(start);
		actor.visitLineNumber(0, start);

		// call Object.super(this);
		actor.visitVarInsn(ALOAD, 0);
		actor.visitMethodInsn(INVOKESPECIAL, InternalName.OBJECT.qualifiedName(), "<init>", "()V", false);

		for(StaticField f : type.fields.keys()) {
			if(f.ownerType.equals(getType().toInternalName()) && f instanceof ObjectField) {
				// push "this"
				actor.visitVarInsn(ALOAD, 0);
				if(type.fields.get(f) != null) {
					final Expression xpr = new Expression(type, type.fields.get(f).expression(), actor);
					xpr.push(actor);
				} else {
					if(f.type.isBaseType())
						f.type.toBaseType().getDefaultValue().push(actor);
					else
						actor.visitInsn(ACONST_NULL);
				}
				actor.visitFieldInsn(PUTFIELD, getType().toInternalName().qualifiedName(), f.name.text, f.type.arrayName());
			}
		}
		final Label finish = new Label();
		actor.visitLabel(finish);
		actor.visitInsn(RETURN);
		// actor.visitLocalVariable("this", type.toInternalName().objectString(), null, start, finish, 0);
		actor.visitMaxs(0, 0);
		actor.visitEnd();
	}

	public Type resolveImport(InternalName name) {
		return resolveImportObject(name);
	}

	public Type resolveImport(String name) {
		return resolveImport(name);
	}

	// private because Object should be a union of String & InternalName
	private Type resolveImportObject(Object obj) {
		for(Type t : imports)
			if(t.name.equals(obj))
				return t;
		return null;
	}

}
