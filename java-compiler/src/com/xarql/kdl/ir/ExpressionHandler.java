package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.MethodVisitor;

import static com.xarql.kdl.names.BaseType.STRING;

public interface ExpressionHandler extends CommonText {

	public static final MethodHeader INIT_STRING_BUILDER = new MethodHeader(new InternalName(StringBuilder.class), MethodHeader.S_INIT, null, null, ACC_PUBLIC);
	MethodHeader SB_APPEND = new MethodHeader(new InternalName(StringBuilder.class), "append", MethodHeader.toParamList(new InternalName(String.class)),
			new ReturnValue(new InternalName(StringBuilder.class)), ACC_PUBLIC);
	MethodHeader SB_TO_STRING = new MethodHeader(new InternalName(StringBuilder.class), "toString", null, ReturnValue.STRING, ACC_PUBLIC);

	public static InternalName compute(final Expression xpr, final Actor actor) throws Exception {
		final Pushable res = xpr.a;
		final Pushable calc = xpr.b;
		final Operator opr = xpr.opr;

		if(xpr.isSingleValue()) {
			return res.pushType(actor);
		} else {
			switch(res.toBaseType()) {
				case INT:
				case SHORT:
				case BYTE:
				case BOOLEAN: {
					computeInt(res, calc, opr, actor);
					return InternalName.INT;
				}
				case STRING: {
					computeString(res, calc, opr, actor);
					return InternalName.STRING;
				}
				default:
					throw new UnimplementedException(res.toString());
			}
		}
	}

	/**
	 * Puts two new StringBuilders on the stack
	 *
	 * @param visitor
	 */
	public static void createStringBuilder(MethodVisitor visitor) {
		visitor.visitTypeInsn(NEW, new InternalName(StringBuilder.class).nameString());
		visitor.visitInsn(DUP);
		INIT_STRING_BUILDER.invoke(visitor);
	}

	public static void computeString(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		switch(opr) {
			case ADD: {
				switch(res2.toBaseType()) {
					case INT: {
						createStringBuilder(actor);
						res1.push(actor);
						SB_APPEND.invoke(actor);
						res2.push(actor);
						CompilationUnit.convertToString(res2.toBaseType().toInternalName(), actor);
						SB_APPEND.invoke(actor);
						SB_TO_STRING.invoke(actor);
						break;
					}
					case STRING: {
						createStringBuilder(actor);
						res1.push(actor);
						SB_APPEND.invoke(actor);
						res2.push(actor);
						SB_APPEND.invoke(actor);
						SB_TO_STRING.invoke(actor);
						break;
					}
					default: {
						throw new UnimplementedException(SWITCH_BASETYPE);
					}
				}
			}
				break;
			default: {
				throw new UnimplementedException(SWITCH_OPERATOR);
			}
		}
	}

	public static BaseType computeInt(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		if(res2.toBaseType() == STRING)
			throw new IncompatibleTypeException(BaseType.INT + INCOMPATIBLE + STRING);
		// under the hood booleans should be either 0 or 1
		else {
			res1.push(actor);
			res2.push(actor);
			switch(opr) {
				case ADD:
					actor.visitInsn(IADD);
					break;
				case SUB:
					actor.visitInsn(ISUB);
					break;
				case MUL:
					actor.visitInsn(IMUL);
					break;
				case DIV:
					actor.visitInsn(IDIV);
					break;
				case MOD:
					actor.visitInsn(IREM);
					break;
				default:
					throw new UnimplementedException(SWITCH_OPERATOR);
			}
		}
		return BaseType.INT;
	}

}
