package main.com.xarql.kdl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassCreator implements Opcodes {
    public static final int CONST = ACC_PUBLIC + ACC_STATIC + ACC_FINAL;

    private String name;
    private File   source;
    private String pkg;

    final BestList<String> tokens;

    public static void main(String[] args) throws IOException, Exception {
        ClassCreator cc = new ClassCreator(Lexer.TEST_SOURCE);
        cc.write();
    }

    public ClassCreator(File source) throws IOException {
        this.source = source;
        tokens = Lexer.getTokens(source);

        System.out.println("done!");
    }

    public void write() {
        try {
            Files.write(source.toPath().resolveSibling(name + ".class"), create());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] create() throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MethodVisitor mv;
        AnnotationVisitor av0;

        String internalName;
        if(pkg != null)
            internalName = pkg.replace('.', '/') + '/' + name;
        else
            internalName = name;
        String internalObjectName = 'L' + internalName + ';';
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, internalName, null, NameFormats.internalName(Object.class), null);
        cw.visitSource(name + ".kdl", null);

        addStringConstant(cw, "HELLO_WORLD", "hello world");

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(3, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", internalObjectName, null, l0, l1, 0);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();

            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(7, l0);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("hello world");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(8, l1);
            mv.visitInsn(RETURN);

            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("args", "[Ljava/lang/String;", null, l0, l2, 0);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();

        return cw.toByteArray();
    }

    public static void addStringConstant(ClassWriter cw, String constName, String str) {
        NameFormats.checkKDLConstName(constName);
        FieldVisitor fv = cw.visitField(CONST, constName, NameFormats.internalObjectName(String.class), null, str);
        fv.visitEnd();
    }

}
