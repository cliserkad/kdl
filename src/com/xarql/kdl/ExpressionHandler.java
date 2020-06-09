package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import org.objectweb.asm.Opcodes;

import static com.xarql.kdl.JavaMethodDef.TO_STRING;
import static com.xarql.kdl.SourceListener.parseLiteral;
import static com.xarql.kdl.SourceListener.standardHandle;
import static com.xarql.kdl.names.InternalName.internalName;

public class ExpressionHandler implements CommonNames, Opcodes {
	private final SourceListener parent;

	public ExpressionHandler(SourceListener parent) {
		this.parent = parent;
	}

	public BaseType compute(final Expression xpr, final LinedMethodVisitor lmv) {
		final Value val1 = xpr.partA;
		final Value val2 = xpr.partB;
		final Operator opr = xpr.operator;

		if(!val1.isBaseType() || !val2.isBaseType()) {
			standardHandle(new UnimplementedException("Custom expressions are not implemented"));
			return null;
		}
		else {
			switch(val1.toBaseType()) {
				case INT:
				case BOOLEAN: {
					parent.pushValue(val1, lmv);
					parent.pushValue(val2, lmv);
					computeInt(val2, opr, lmv);
					return INT;
				}
				case STRING: {
					computeString(val1, val2, opr, lmv);
					return STRING;
				}
				default:
					standardHandle(new UnimplementedException(SWITCH_BASETYPE));
					return null;
			}
		}
	}

	/**
	 * Puts two new StringBuilders on the stack
	 * @param lmv
	 */
	private static void stringBuilderInit(LinedMethodVisitor lmv) {
		lmv.visitTypeInsn(NEW, internalName(StringBuilder.class).stringOutput());
		lmv.visitInsn(DUP);
		lmv.visitMethodInsn(INVOKESPECIAL, internalName(StringBuilder.class).stringOutput(), INIT, NO_PARAM_VOID, false);
	}

	private void computeString(Value val1, Value val2, Operator opr, LinedMethodVisitor lmv) {
		switch(opr) {
			case PLUS: {
				switch(val2.toBaseType()) {
					case INT: {
						stringBuilderInit(lmv);
						parent.pushValue(val1, lmv);
						lmv.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
						parent.pushValue(val2, lmv);
						parent.convertToString(val2.toBaseType().toInternalObjectName(), lmv);
						lmv.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
						lmv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_IN_S, TO_STRING.methodName, TO_STRING.descriptor(), false);
						break;
					}
					case STRING: {
						stringBuilderInit(lmv);
						parent.pushValue(val1, lmv);
						lmv.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
						parent.pushValue(val2, lmv);
						lmv.visitMethodInsn(INVOKEVIRTUAL, SB_APPEND.owner(), SB_APPEND.methodName, SB_APPEND.descriptor(), false);
						lmv.visitMethodInsn(INVOKEVIRTUAL, STRING_BUILDER_IN_S, TO_STRING.methodName, TO_STRING.descriptor(), false);
						break;
					}
				}
				break;
			}
			default: {
				System.err.println(lmv.getLine());
				SourceListener.standardHandle(new UnimplementedException("Only + has been implemented for strings"));
			}
		}
	}

	private static void computeInt(Value val2, Operator opr, LinedMethodVisitor lmv) {
		if(val2.toBaseType() == STRING)
			standardHandle(new IncompatibleTypeException("strings can not be modifiers to ints"));
			// under the hood booleans should be either 0 or 1
		else if(val2.toBaseType() == INT || val2.toBaseType() == BOOLEAN) {
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
					standardHandle(new UnimplementedException(SWITCH_OPERAOTR));
			}
		}
	}

	public static Literal<?> evaluateLiterals(final kdlParser.CompileTimeExpressionContext ctx) {
		final Literal<?> litA = parseLiteral(ctx.literal(0));
		final Literal<?> litB = parseLiteral(ctx.literal(1));
		final Operator opr = Operator.match(ctx.operator().getText());

		switch(litA.toBaseType()) {
			case INT: {
				if(!(litB.value instanceof Integer))
					standardHandle(new IncompatibleTypeException(litB.value + " can not be a modifier to an int"));
				final int a = (Integer) litA.value;
				final int b = (Integer) litB.value;

				switch(opr) {
					case PLUS:
						return new Literal<>(a + b);
					case MINUS:
						return new Literal<>(a - b);
					case MULTIPLY:
						return new Literal<>(a * b);
					case DIVIDE:
						return new Literal<>(a / b);
					case MODULUS:
						return new Literal<>(a % b);
					default:
						standardHandle(new UnimplementedException(SWITCH_OPERAOTR));
						break;
				}
			}
			case STRING: {
				String a = (String) litA.value;
				switch(litB.toBaseType()) {
					case INT: {
						final int b = (Integer) litB.value;
						switch(opr) {
							case PLUS:
								return new Literal<>(a + b);
							case MINUS:
								return new Literal<>(a.substring(0, a.length() - b));
							case MULTIPLY: {
								String out = "";
								for(int i = 0; i < b; i++)
									out += a;
								return new Literal<>(out);
							}
							case DIVIDE:
								return new Literal<>(a.substring(0, a.length() / b));
							case MODULUS:
								return new Literal<>("" + a.charAt(b));
							default:
								standardHandle(new UnimplementedException(SWITCH_OPERAOTR));
								return null;
						}
					}
					case STRING: {
						final String b = (String) litB.value;
						switch(opr) {
							case PLUS:
								return new Literal<>(a + b);
							case MINUS:
								return new Literal<>(a.replace(b, ""));
							case MULTIPLY:
								standardHandle(new IncompatibleTypeException("Can't use the multiply operator (*) if both sides are strings"));
								return null;
							case DIVIDE:
								standardHandle(new IncompatibleTypeException("Can't use the division operator (/) if both sides are strings"));
								return null;
							case MODULUS:
								standardHandle(new IncompatibleTypeException("Can't use the modulus operator (%) if both sides are strings"));
								return null;
							default:
								standardHandle(new UnimplementedException(SWITCH_OPERAOTR));
								return null;
						}
					}
					case BOOLEAN: {
						standardHandle(new IncompatibleTypeException("Booleans do not have any operators (yet)"));
					}
				}
			}
		}
		return null;
	}

}
