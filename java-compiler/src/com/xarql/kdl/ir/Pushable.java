package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.SymbolResolutionException;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.Details;
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
		else if(val.addressable() != null && val.addressable().ID().size() == 1) {
			final String id = val.addressable().ID(0).getText();
			if(actor.unit.hasLocalVariable(id))
				return actor.unit.getLocalVariable(id);
			else if(actor.unit.hasConstant(id))
				return actor.unit.getConstant(id);
			else
				throw new SymbolResolutionException(id);
		}
		else if(val.indexAccess() != null)
			return new IndexAccess(actor.unit.getLocalVariable(val.indexAccess().ID().getText()), new Expression(val.indexAccess().expression(), actor));
		else if(val.subSequence() != null)
			return new SubSequence(val.subSequence(), actor);
		else if(val.arrayLength() != null)
			return new ArrayLength(actor.unit.getLocalVariable(val.arrayLength().ID().getText()));
		else if(val.R_NULL() != null)
			return new Null();
		else if(val.methodCall() != null) {
			// FIXME this code is duplicated from consumeMethodCall() in CompilationUnit
			final kdl.MethodCallContext mtd = val.methodCall();
			final String methodName = mtd.addressable().ID().get(mtd.addressable().ID().size() - 1).getText();
			if(actor.unit.isImported(methodName))
				return new NewObject(mtd, actor);
			else
				return new MethodCall(mtd, actor);
		} else if(val.addressable().ID().size() > 1) {
			// FIXME dirty way of getting suspected classname and varname
			final String className = val.addressable().ID(0).getText();
			final String varname = val.addressable().ID(1).getText();
			final StaticField field = actor.unit.fields().equivalentKey(new StaticField(varname, actor.unit.resolveAgainstImports(className)));
			if(field != null)
				return field;
			else
				throw new SymbolResolutionException(className + "." + varname + "\nFull text: " + val.getText());
		} else
			throw new UnimplementedException("a type of Pushable wasn't parsed correctly\n The input text was \"" + val.getText() + "\"");
	}

}
