package com.xarql.kdl.calculable;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

import static com.xarql.kdl.names.BaseType.STRING;

public interface ExpressionHandler extends CommonText {
    JavaMethodDef INIT_STRING_BUILDER = new JavaMethodDef(new InternalName(StringBuilder.class), JavaMethodDef.INIT, null, null, ACC_PUBLIC);

    public static ToName compute(final Expression xpr, final MethodVisitor visitor) throws Exception {
        final Resolvable res = xpr.a;
        final Calculable calc = xpr.b;
        final Operator opr = xpr.opr;

        if(xpr.isSingleValue()) {
            res.push(visitor);
            return res;
        }
        else {
            switch(res.toBaseType()) {
                case INT:
                case BOOLEAN: {
                    computeInt(res, calc, opr, visitor);
                    return BaseType.INT;
                }
                case STRING: {
                    return computeString(res, calc, opr, visitor);
                }
                default:
                    throw new UnimplementedException(SWITCH_BASETYPE);
            }
        }
    }

    /**
     * Puts two new StringBuilders on the stack
     * @param visitor
     */
    public static void stringBuilderInit(MethodVisitor visitor) {
        visitor.visitTypeInsn(NEW, new InternalName(StringBuilder.class).internalName());
        visitor.visitInsn(DUP);
        INIT_STRING_BUILDER.invoke(visitor);
    }

    public static BaseType computeString(Resolvable res1, Calculable res2, Operator opr, MethodVisitor visitor) throws Exception {
        switch(opr) {
            case PLUS: {
                switch(res2.toBaseType()) {
                    case INT: {
                        stringBuilderInit(visitor);
                        res1.push(visitor);
                        visitor.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
                        res2.calc(visitor);
                        CompilationUnit.convertToString(res2.toBaseType().toInternalName(), visitor);
                        visitor.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
                        visitor.visitMethodInsn(INVOKEVIRTUAL, InternalName.STRING_BUILDER.internalName(), SB_TO_STRING.methodName, SB_TO_STRING.descriptor(), false);
                        return STRING;
                    }
                    case STRING: {
                        stringBuilderInit(visitor);
                        res1.push(visitor);
                        visitor.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
                        res2.calc(visitor);
                        visitor.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
                        visitor.visitMethodInsn(INVOKEVIRTUAL, InternalName.STRING_BUILDER.internalName(), SB_TO_STRING.methodName, SB_TO_STRING.descriptor(), false);
                        return STRING;
                    }
                }
            }
            default: {
                throw new UnimplementedException(SWITCH_OPERATOR);
            }
        }
    }

    public static BaseType computeInt(Resolvable res1, Calculable res2, Operator opr, MethodVisitor visitor) throws Exception {
        if(res2.toBaseType() == STRING)
            throw new IncompatibleTypeException(BaseType.INT + INCOMPATIBLE + STRING);
            // under the hood booleans should be either 0 or 1
        else {
            res1.push(visitor);
            res2.calc(visitor);
            switch(opr) {
                case PLUS:
                    visitor.visitInsn(IADD);
                    break;
                case MINUS:
                    visitor.visitInsn(ISUB);
                    break;
                case MULTIPLY:
                    visitor.visitInsn(IMUL);
                    break;
                case DIVIDE:
                    visitor.visitInsn(IDIV);
                    break;
                case MODULUS:
                    visitor.visitInsn(IREM);
                    break;
                default:
                    throw new UnimplementedException(SWITCH_OPERATOR);
            }
        }
        return BaseType.INT;
    }

}
