package com.xarql.kdl.calculable;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.LinedMethodVisitor;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;

import static com.xarql.kdl.names.InternalName.internalName;

public interface ExpressionHandler extends CommonNames {

    public static ToName compute(final Expression xpr, final LinedMethodVisitor lmv) throws Exception {
        final Resolvable res1 = xpr.a;
        final Resolvable res2 = xpr.b;
        final Operator opr = xpr.opr;

        if(xpr.isSingleValue()) {
            res1.push(lmv);
            return res1;
        }
        else {
            switch(res1.toBaseType()) {
                case INT:
                case BOOLEAN: {
                    computeInt(res1, res2, opr, lmv);
                    return INT;
                }
                case STRING: {
                    return computeString(res1, res2, opr, lmv);
                }
                default:
                    throw new UnimplementedException(SWITCH_BASETYPE);
            }
        }
    }

    /**
     * Puts two new StringBuilders on the stack
     * @param lmv
     */
    public static void stringBuilderInit(LinedMethodVisitor lmv) {
        lmv.visitTypeInsn(NEW, internalName(StringBuilder.class).stringOutput());
        lmv.visitInsn(DUP);
        lmv.visitMethodInsn(INVOKESPECIAL, internalName(StringBuilder.class).stringOutput(), INIT, NO_PARAM_VOID, false);
    }

    public static BaseType computeString(Resolvable res1, Resolvable res2, Operator opr, LinedMethodVisitor lmv) throws Exception {
        switch(opr) {
            case PLUS: {
                switch(res2.toBaseType()) {
                    case INT: {
                        stringBuilderInit(lmv);
                        res1.push(lmv);
                        lmv.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
                        res2.push(lmv);
                        CompilationUnit.convertToString(res2.toBaseType().toInternalObjectName(), lmv);
                        lmv.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
                        lmv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_IN_S, SB_TO_STRING.methodName, SB_TO_STRING.descriptor(), false);
                        return STRING;
                    }
                    case STRING: {
                        stringBuilderInit(lmv);
                        res1.push(lmv);
                        lmv.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
                        res2.push(lmv);
                        lmv.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
                        lmv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_IN_S, SB_TO_STRING.methodName, SB_TO_STRING.descriptor(), false);
                        return STRING;
                    }
                }
            }
            default: {
                throw new UnimplementedException(SWITCH_OPERAOTR);
            }
        }
    }

    public static BaseType computeInt(Resolvable res1, Resolvable res2, Operator opr, LinedMethodVisitor lmv) throws Exception {
        if(res1.toBaseType() == STRING)
            throw new IncompatibleTypeException(INT + INCOMPATIBLE + STRING);
            // under the hood booleans should be either 0 or 1
        else if(res2.toBaseType() == INT || res2.toBaseType() == BOOLEAN) {
            switch(opr) {
                case PLUS:
                    lmv.visitInsn(IADD);
                    break;
                case MINUS:
                    lmv.visitInsn(ISUB);
                    break;
                case MULTIPLY:
                    lmv.visitInsn(IMUL);
                    break;
                case DIVIDE:
                    lmv.visitInsn(IDIV);
                    break;
                case MODULUS:
                    lmv.visitInsn(IREM);
                    break;
                default:
                    throw new UnimplementedException(SWITCH_OPERAOTR);
            }
        }
        return INT;
    }

}
