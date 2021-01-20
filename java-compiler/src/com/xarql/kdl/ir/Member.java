package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.SymbolResolutionException;
import com.xarql.kdl.Text;
import com.xarql.kdl.Type;
import com.xarql.kdl.names.Details;

/**
 * Anything with an Identifier. A Member can belong to a Type or Scope
 */
public interface Member extends Pushable {

	/**
	 * A method that provides the details of the member
	 */
	Details details();

	static Member resolveID(Type sourceType, String id, Actor actor) throws SymbolResolutionException {
		if(Character.isDigit(id.charAt(0)))
			actor.unit.warn("type names shouldn't start with a digit, but " + id + " was found");

		if(sourceType == null) {
			if(actor.scope.contains(id))
				return actor.scope.get(id);
			else
				sourceType = actor.unit.type;
		}

		final Member out = sourceType.members().get(new Identifier(id));
		if(out == null)
			throw new SymbolResolutionException("No member in " + sourceType + " matched " + id);
		if(out instanceof Type && Text.isFirstLetterLowercase(id))
			actor.unit.warn("type names shouldn't start with a lowercase letter, but " + id + " was found");
		else if(out instanceof Constant && Text.hasLowercase(id))
			actor.unit.warn("const names shouldn't have lowercase letters, but " + id + " was found");
		return out;
	}

}
