package com.xarql.kdl.ir;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.ToName;

/**
 * Represents anything that may be pushed on to the JVM stack. Resolvables are a
 * type of Calculable that do not require any instructions to be executed after
 * pushing. After the push method is invoked, only 1 value should be added to
 * the stack.
 */
public interface Pushable extends ToName {

	/**
	 * Pushes this value on to the stack. Executes sub-pushes and instructions if
	 * needed. Use this over pushType() whenever an InternalName is not required.
	 *
	 * @param actor any Actor
	 * @return instance of implementing class; whatever "this" is
	 * @throws Exception if pushing is impossible
	 */
	public Pushable push(final Actor actor) throws Exception;

	/**
	 * Attempts to parse a Resolvable symbol
	 *
	 * @param actor any Actor
	 * @param value value context
	 * @return A Resolvable whose actual type corresponds to the symbol
	 * @throws UnimplementedException thrown if missing a symbol from the grammar
	 */
	public static Pushable parse(final Actor actor, final kdl.ValueContext value) throws Exception {
		if(value.literal() != null)
			return Literal.parseLiteral(value.literal(), actor);
		else
			return parse(actor, actor.unit.type, value);
	}

	public static Pushable parse(final Actor actor, final Type parentType, final kdl.ValueContext value) throws Exception {
		if(value.IDENTIFIER() != null) {
			return parseID(actor.unit.type, value.IDENTIFIER().getText(), false);
		} else if(value.methodCall() != null) {
			return parseID(actor.unit.type, value.methodCall().IDENTIFIER().getText(), true);
		}
		else
			throw new UnimplementedException("a type of Pushable wasn't parsed correctly\n The input text was \"" + value.getText() + "\"");
	}

	public static Pushable parseID(Type sourceType, String id, boolean isMethod) throws SymbolResolutionException {
		for(Member m : sourceType.members()) {
			if(m.details().name.equals(id) && (m instanceof MethodHeader) == isMethod)
				return m;
		}
		throw new SymbolResolutionException("Couldn't find identifier " + id + " within " + sourceType);
	}

}
