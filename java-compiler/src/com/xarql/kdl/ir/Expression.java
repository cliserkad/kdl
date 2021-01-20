package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.TypeDescriptor;

import static com.xarql.kdl.names.BaseType.STRING;

public class Expression implements Pushable, CommonText {

	public static final MethodHeader INIT_STRING_BUILDER = new MethodHeader(Type.get(StringBuilder.class), MethodHeader.S_INIT, null, null, ACC_PUBLIC);
	public static final MethodHeader SB_APPEND = new MethodHeader(Type.get(StringBuilder.class), "append", MethodHeader.toParamList(new TypeDescriptor(String.class)),
			new TypeDescriptor(StringBuilder.class), ACC_PUBLIC);
	public static final MethodHeader SB_TO_STRING = new MethodHeader(Type.get(StringBuilder.class), "toString", null, BaseType.STRING.toTypeDescriptor(), ACC_PUBLIC);

	public final Pushable value;
	public final Operator operator;
	public final Expression expression;

	public Expression(Pushable value, Expression expression, Operator operator) {
		this.value = value;
		this.operator = operator;
		this.expression = expression;
	}

	public Expression(Pushable value) {
		this(value, (Expression) null, null);
	}

	public static Pushable resolveID(Identifier id, Type encloser, Actor actor) throws SymbolResolutionException {
		if(actor.scope.contains(id.text))
			return actor.scope.get(id.text);
		else {
			if(encloser.members().get(id) != null)
				return encloser.members().get(id);
			else
				throw new SymbolResolutionException("Couldn't match " + id + " to a member within " + encloser);
		}
	}

	public Expression(Type parent, kdl.ExpressionContext ctx, Actor actor) throws Exception {
		if(ctx.value().literal() != null)
			value = Literal.parseLiteral(ctx.value().literal(), actor);
		else if(ctx.value().IDENTIFIER() != null)
			value = resolveID(new Identifier(ctx.value().IDENTIFIER().getText()), parent.toType(), actor);
		else if(ctx.value().methodCall() != null)
			value = new MethodCall(parent, ctx.value().methodCall(), actor);
		else if(ctx.value().THIS() != null) {
			value = actor.scope.get("this");
		} else {
			throw new Exception("Failed to parse value of expression. Check kdl.g4 for updates");
		}

		if(ctx.expression() != null) {
			try {
				expression = new Expression(value.toType(), ctx.expression(), actor);
			} catch(SymbolResolutionException sre) {
				throw new SymbolResolutionException("When attempting to instantiate nested expression using parent of " + value + ":\n" + sre.getMessage());
			}
		} else
			this.expression = null;

		if(ctx.operator() != null)
			operator = Operator.match(ctx.operator().getText());
		else
			operator = null;

		if(operator != null && expression == null) {
			throw new IllegalStateException("Expressions must have a right side if they have an operator");
		}
	}

	public boolean isSingleValue() {
		return value != null && expression == null && operator == null;
	}

	public Assignable declaringMember() throws Exception {
		return null;
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		if(isSingleValue()) {
			return value.push(actor);
		} else {
			switch(toBaseType()) {
				case INT:
				case BOOLEAN:
					computeInt(value, expression, operator, actor);
					break;
				case STRING:
					computeString(value, expression, operator, actor);
					break;
				default:
					throw new UnimplementedException(SWITCH_BASETYPE);
			}
		}
		return this;
	}

	/**
	 * Puts two new StringBuilders on the stack
	 */
	public static void createStringBuilder(Actor actor) throws Exception {
		actor.visitTypeInsn(NEW, new TypeDescriptor(StringBuilder.class).qualifiedName());
		actor.visitInsn(DUP);
		INIT_STRING_BUILDER.push(actor);
	}

	public static void computeString(Pushable res1, Pushable res2, Operator opr, Actor actor) throws Exception {
		switch(opr) {
			case PLUS: {
				switch(res2.toBaseType()) {
					case INT: {
						createStringBuilder(actor);
						res1.push(actor);
						SB_APPEND.push(actor);
						res2.push(actor);
						CompilationUnit.convertToString(res2.toTypeDescriptor(), actor);
						SB_APPEND.push(actor);
						SB_TO_STRING.push(actor);
						break;
					}
					case STRING: {
						createStringBuilder(actor);
						res1.push(actor);
						SB_APPEND.push(actor);
						res2.push(actor);
						SB_APPEND.push(actor);
						SB_TO_STRING.push(actor);
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
				case PLUS:
					actor.visitInsn(IADD);
					break;
				case MINUS:
					actor.visitInsn(ISUB);
					break;
				case MULTIPLY:
					actor.visitInsn(IMUL);
					break;
				case DIVIDE:
					actor.visitInsn(IDIV);
					break;
				case MODULUS:
					actor.visitInsn(IREM);
					break;
				default:
					throw new UnimplementedException(SWITCH_OPERATOR);
			}
		}
		return BaseType.INT;
	}

	@Override
	public Type toType() {
		return value.toType();
	}

	@Override
	public boolean isBaseType() {
		return value.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return value.toBaseType();
	}

	public String toString() {
		return value + " " + operator + " " + expression;
	}

	@Override
	public TypeDescriptor toTypeDescriptor() {
		return value.toTypeDescriptor();
	}

}
