package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.InternalName;
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
	 * Pushes this value on to the stack. Returns the type of the value. Should call
	 * push().
	 *
	 * @param actor any Actor
	 * @return pushed value type
	 * @throws Exception if pushing is impossible
	 * @see Pushable#push(Actor)
	 */
	public InternalName pushType(final Actor actor) throws Exception;

	/**
	 * Attempts to parse a Resolvable symbol
	 *
	 * @param actor any Actor
	 * @param val   The symbol
	 * @return A Resolvable whose actual type corresponds to the symbol
	 * @throws UnimplementedException thrown if missing a symbol from the grammar
	 */
	public static Pushable parse(final Actor actor, final kdl.ValueContext val) throws Exception {
		if(val.literal() != null)
			return Literal.parseLiteral(val.literal(), actor);
		else if(val.constant() != null)
			return actor.unit.getConstant(val.constant().CONSTNAME().getText());
		else if(val.variable() != null)
			return actor.unit.getLocalVariable(val.variable().VARNAME().getText());
		else if(val.indexAccess() != null)
			return new IndexAccess(actor.unit.getLocalVariable(val.indexAccess().VARNAME().getText()), new Expression(val.indexAccess().expression(), actor));
		else if(val.subSequence() != null)
			return new SubSequence(val.subSequence(), actor);
		else if(val.arrayLength() != null)
			return new ArrayLength(actor.unit.getLocalVariable(val.arrayLength().VARNAME().getText()));
		else if(val.R_NULL() != null)
			return new Null();
		else if(val.methodCall() != null)
			return new MethodCall(val.methodCall(), actor);
		else if(val.newObject() != null)
			return new NewObject(val.newObject(), actor);
		else
			throw new UnimplementedException("a type of Pushable wasn't parsed correctly\n The input text was \"" + val.getText() + "\"");
	}

}
