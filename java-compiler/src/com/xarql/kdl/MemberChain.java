package com.xarql.kdl;

import com.xarql.kdl.ir.Assignable;
import com.xarql.kdl.ir.Pushable;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;

public class MemberChain implements Pushable, Assignable {

    public final Member[] chain;

    public MemberChain(BestList<Member> chain) {
        this.chain = chain.toArray(new Member[0]);
    }

    public Member last() {
        return chain[chain.length - 1];
    }

    @Override
    public MemberChain push(Actor actor) throws Exception {
        for(Member m : chain)
            m.push(actor);
        return this;
    }

    @Override
    public InternalName pushType(Actor actor) throws Exception {
        return push(actor).toInternalName();
    }

    @Override
    public InternalName toInternalName() {
        return null;
    }

    @Override
    public boolean isBaseType() {
        return false;
    }

    @Override
    public BaseType toBaseType() {
        return null;
    }

    @Override
    public Assignable assign(InternalName incomingType, Actor actor) throws Exception {
        push(actor);
        if(incomingType.compatibleWith(last().toInternalName())) {
            if(last() instanceof Assignable)
                return ((Assignable) last()).assign(incomingType, actor);
            else
                throw new IllegalStateException(last() + " is not Assignable");
        }
        else
            throw new IllegalArgumentException(incomingType + " is incompatible with " + last().toInternalName());
    }

    @Override
    public Assignable assignDefault(Actor actor) throws Exception {
        if(last() instanceof Assignable)
            return ((Assignable) last()).assignDefault(actor);
        else
            throw new IllegalStateException(last() + " is not Assignable");
    }
}
