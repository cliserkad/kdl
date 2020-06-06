package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlLexer;
import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.NameFormats;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ClassCreator implements Opcodes {
	public static final int CONST = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL;

	public static final File                DEFAULT_LOC = new File(System.getProperty("user.home") + "/Documents/kdl/");
	// set in constructor
	private final       File                input;
	private final       ClassWriter         cw;
	private final       BestList<Constant>  constants;
	private final       BestList<Import>    imports;
	private final       BestList<MethodDef> methods;
	public              Scope               currentScope;
	private             SourceListener      sl;
	private             String              className;
	private             boolean             nameSet;

	public ClassCreator(final File input) {
		this.input = input;
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		constants = new BestList<>();
		imports = new BestList<>();
		methods = new BestList<>();
	}

	public static void main(String[] args) {
		for(File f : DEFAULT_LOC.listFiles()) {
			if(f.getName().endsWith(".kdl")) {
				ClassCreator cc = new ClassCreator(f);
				if(!cc.build())
					System.err.println("Failed to read IO");
				cc.write();
			}
		}
	}

	public LocalVariable getLocalVariable(String name) {
		return currentScope.getVariable(name);
	}

	public void addMethodDef(MethodDef md) {
		methods.add(md);
	}

	public boolean hasConstant(final String name) {
		return resolveConstant(name) != null;
	}

	public Constant resolveConstant(final String name) {
		for(Constant c : constants) {
			if(c.name.equals(name))
				return c;
		}
		throw new IllegalArgumentException("Constant " + name + " does not exist");
	}

	public boolean build() {
		try {
			final String fileContent = new String(Files.readAllBytes(input.toPath()));
			final kdlLexer lex = new kdlLexer(CharStreams.fromString(fileContent));
			final CommonTokenStream tokens = new CommonTokenStream(lex);
			final kdlParser parser = new kdlParser(tokens);
			final ParseTree tree = parser.source();
			sl = new SourceListener(this);
			sl.newPass();
			ParseTreeWalker.DEFAULT.walk(sl, tree);
			sl.newPass();
			ParseTreeWalker.DEFAULT.walk(sl, tree);
			return true;
		} catch(final IOException ioe) {
			return false;
		}
	}

	public void write() {
		try {
			cw.visitEnd();
			Files.write(input.toPath().resolveSibling(className + ".class"), cw.toByteArray());
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the name of this class to the given className.
	 * @param className name of class, Ex. Test
	 * @return success of operation
	 */
	public boolean setClassName(final String className) {
		if(!nameSet) {
			this.className = className;
			nameSet = true;

			// give name to ClassWriter
			cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, internalName(), null, NameFormats.internalName(Object.class), null);
			cw.visitSource(className + ".kdl", null);

			return true;
		}
		else
			return false;
	}

	public String internalObjectName() {
		return "L" + className + ";";
	}

	public String internalName() {
		return className;
	}

	public LinedMethodVisitor defineMethod(MethodDef md, int line) {
		if(methods.contains(md)) {
			currentScope = new Scope("Method " + md.methodName + " of class " + className, new Label());
			final MethodVisitor mv = cw.visitMethod(md.access, md.methodName, md.descriptor(), null, null);
			mv.visitCode();
			return new LinedMethodVisitor(mv, line);
		}
		else
			throw new IllegalArgumentException("None of the detected method definitions match the given method definition");
	}

	public Import resolveClassName(String className) {
		for(Import imp : imports)
			if(imp.className.equals(className))
				return imp;
		return null;
	}

	public void addImport(final Import imp) {
		imports.add(imp);
	}

	public boolean addConstant(final Constant<?> c) {
		if(constants.contains(c))
			return false;
		FieldVisitor fv;
		if(c.value instanceof String)
			fv = cw.visitField(CONST, c.name, NameFormats.internalObjectName(String.class), null, c.value.toString());
		else if(c.value instanceof Boolean)
			fv = cw.visitField(CONST, c.name, BaseType.BOOLEAN.stringOutput(), null, c.value);
		else if(c.value instanceof Integer)
			fv = cw.visitField(CONST, c.name, BaseType.INT.stringOutput(), null, c.value);
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
		mv.visitLocalVariable("this", internalObjectName(), null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

}
