package com.xarql.kdl;

import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.ir.Constant;
import com.xarql.kdl.ir.Pushable;
import com.xarql.kdl.ir.StaticField;
import com.xarql.kdl.names.Details;

import java.lang.reflect.Field;

/**
 * Represents a constant, field or method which is a "Member" of a Type
 */
public interface Member extends Pushable {

    /**
     * A method that provides the details of the member
     */
    public abstract Details details();

    public static MemberChain parseMember(final kdl.MemberContext member, final Actor actor) throws SymbolResolutionException {
        BestList<kdl.MemberContext> chain = new BestList<>(member);
        while(chain.last()() != null) {
            chain.add(chain.last()());
        }

        Type parent = null;
        BestList<Member> out = new BestList<>();
        for(kdl.MemberContext ctx : chain) {
            out.add(resolveID(parent, ctx.IDENTIFIER().getText(), actor));
            parent = actor.unit.type.resolveImportOrFail(out.last().details().type.name());
        }
        return new MemberChain(out);
    }

    public static Member resolveID(Type parent, String id, Actor actor) throws SymbolResolutionException {
        if(Character.isDigit(id.charAt(0)))
            actor.unit.warn("type names shouldn't start with a digit, but " + id + " was found");

        if(parent == null) {
            if(actor.unit.getCurrentScope().contains(id))
                return actor.unit.getCurrentScope().getVariable(id);
            else
                parent = actor.unit.type;
        }

        final Member out = parent.members().get(id);
        if(out == null)
            throw new SymbolResolutionException("No member in " + parent + " matched " + id);
        if(out instanceof Type && Text.isFirstLetterLowercase(id))
            actor.unit.warn("type names shouldn't start with a lowercase letter, but " + id + " was found");
        else if(out instanceof Constant && Text.hasLowercase(id))
            actor.unit.warn("const names shouldn't have lowercase letters, but " + id + " was found");
        return out;
    }

}
