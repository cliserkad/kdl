package main.com.xarql.kdl;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.objectweb.asm.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ClassCreator implements Opcodes {
	public static final int    CONST              = Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC + Opcodes.ACC_FINAL;
	public static final String BOOLEAN_DESCRIPTOR = "Z";
	public static final String INT_DESCRIPTOR     = "I";

	public static final File DEFAULT_LOC = new File(System.getProperty("user.home") + "/Documents/kdl/Test.kdl");

	// set in constructor
	private final File               input;
	private final ClassWriter        cw;
	private final BestList<Constant> constants;
	private       SourceListener     sl;
	private       String             className;
	private       boolean            nameSet;

	public ClassCreator(final File input) {
		this.input = input;
		cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		constants = new BestList<>();
	}

	public static void main(final String[] args) {
		final ClassCreator cc = new ClassCreator(DEFAULT_LOC);
		if(!cc.build())
			System.out.println("Failed to read IO");
		cc.write();
	}

	public boolean hasConstant(final String name) {
		return constant(name) != null;
	}

	public Constant constant(final String name) {
		System.out.println(constants);
		System.out.println(constants.size());
		for(Constant c : constants) {
			System.out.println(c.name);
			if(c.name.equals(name))
				return c;
		}
		return null;
	}

	public boolean build( ) {
		try {
			final String fileContent = new String(Files.readAllBytes(input.toPath()));
			final kdlLexer lex = new kdlLexer(CharStreams.fromString(fileContent));
			final CommonTokenStream tokens = new CommonTokenStream(lex);
			final kdlParser parser = new kdlParser(tokens);
			final ParseTree tree = parser.source();
			sl = new SourceListener(this);
			ParseTreeWalker.DEFAULT.walk(sl, tree);
			return true;
		} catch(final IOException ioe) {
			return false;
		}
	}

	public void write( ) {
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

	public String internalObjectName( ) {
		return "L" + className + ";";
	}

	public String internalName( ) {
		return className;
	}

	public MethodVisitor addMainMethod( ) {
		final MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		mv.visitCode();
		return mv;
	}

	public void addConstant(final Constant c) {
		FieldVisitor fv;
		if(c.value instanceof StringValue)
			fv = cw.visitField(CONST, c.name, NameFormats.internalObjectName(String.class), null, c.value.toString());
		else if(c.value instanceof BooleanValue)
			fv = cw.visitField(CONST, c.name, BOOLEAN_DESCRIPTOR, null, (boolean) c.value.value());
		else if(c.value instanceof IntegerValue)
			fv = cw.visitField(CONST, c.name, INT_DESCRIPTOR, null, (int) c.value.value());
		else
			throw new UnsupportedOperationException("The class " + c.value.getClass() + " could not be resolved to a const type");
		fv.visitEnd();
		constants.add(c);
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
