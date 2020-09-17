package com.xarql.kdl.ir;

import static com.xarql.kdl.BestList.list;

import org.objectweb.asm.Opcodes;
import com.xarql.kdl.Actor;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;

public class SubSequence extends BasePushable {

	public static final JavaMethodDef SUB_STRING = new JavaMethodDef(InternalName.STRING, "substring", list(InternalName.INT, InternalName.INT), ReturnValue.STRING,
			Opcodes.ACC_PUBLIC);

	public final Variable variable;
	public final Range range;

	public SubSequence(final kdl.SubSequenceContext ctx, final Actor actor) throws Exception {
		this(actor.unit.getLocalVariable(ctx.VARNAME().getText()), new Range(ctx.range(), actor));
	}

	public SubSequence(final Variable variable, final Range range) {
		this.variable = variable;
		this.range = range;
	}

	@Override
	public SubSequence push(Actor visitor) throws Exception {
		if(!variable.isArray() && variable.toBaseType() == BaseType.STRING) {
			variable.push(visitor);
			range.min.push(visitor);
			range.max.push(visitor);
			SUB_STRING.invoke(visitor);
		} else {
			throw new UnimplementedException("Subsequence only implemented for strings");
		}
		return this;
	}

	@Override
	public InternalName toInternalName() {
		return variable.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return variable.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return variable.toBaseType();
	}

}
