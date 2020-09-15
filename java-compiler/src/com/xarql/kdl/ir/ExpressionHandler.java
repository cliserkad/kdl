package com.xarql.kdl.ir;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import org.objectweb.asm.MethodVisitor;

import static com.xarql.kdl.names.BaseType.STRING;

public interface ExpressionHandler extends CommonText {

	JavaMethodDef INIT_STRING_BUILDER = new JavaMethodDef(new InternalName(StringBuilder.class), JavaMethodDef.INIT, null, null, ACC_PUBLIC);

	public static InternalName compute(final Expression xpr, final MethodVisitor visitor) throws Exception {
		final Pushable res = xpr.a;
		final Pushable calc = xpr.b;
		final Operator opr = xpr.opr;

		if (xpr.isSingleValue()) {
			return res.pushType(visitor);
		} else {
			switch (res.toBaseType()) {
				case INT:
				case BOOLEAN: {
					computeInt(res, calc, opr, visitor);
					return InternalName.INT;
				}
				case STRING: {
					computeString(res, calc, opr, visitor);
					return InternalName.STRING;
				}
				default:
					throw new UnimplementedException(SWITCH_BASETYPE);
			}
		}
	}

	/**
	 * Puts two new StringBuilders on the stack
	 * 
	 * @param visitor
	 */
	public static void stringBuilderInit(MethodVisitor visitor) {
		visitor.visitTypeInsn(NEW, new InternalName(StringBuilder.class).internalName());
		visitor.visitInsn(DUP);
		INIT_STRING_BUILDER.invoke(visitor);
	}

	public static void computeString(Pushable res1, Pushable res2, Operator opr, MethodVisitor visitor) throws Exception {
		switch (opr) {
			case PLUS: {
				switch (res2.toBaseType()) {
					case INT: {
						stringBuilderInit(visitor);
						res1.push(visitor);
						visitor.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
						res2.push(visitor);
						CompilationUnit.convertToString(res2.toBaseType().toInternalName(), visitor);
						visitor.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
						visitor.visitMethodInsn(INVOKEVIRTUAL, InternalName.STRING_BUILDER.internalName(), SB_TO_STRING.methodName, SB_TO_STRING.descriptor(), false);
					}
					case STRING: {
						stringBuilderInit(visitor);
						res1.push(visitor);
						visitor.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
						res2.push(visitor);
						visitor.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
						visitor.visitMethodInsn(INVOKEVIRTUAL, InternalName.STRING_BUILDER.internalName(), SB_TO_STRING.methodName, SB_TO_STRING.descriptor(), false);
					}
				}
			}
				break;
			default: {
				throw new UnimplementedException(SWITCH_OPERATOR);
			}
		}
	}

	public static BaseType computeInt(Pushable res1, Pushable res2, Operator opr, MethodVisitor visitor) throws Exception {
		if (res2.toBaseType() == STRING)
			throw new IncompatibleTypeException(BaseType.INT + INCOMPATIBLE + STRING);
		// under the hood booleans should be either 0 or 1
		else {
			res1.push(visitor);
			res2.push(visitor);
			switch (opr) {
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
