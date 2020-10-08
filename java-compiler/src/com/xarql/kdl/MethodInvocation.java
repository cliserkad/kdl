package com.xarql.kdl;

import com.xarql.kdl.ir.Param;
import com.xarql.kdl.ir.Pushable;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.Opcodes;

public class MethodInvocation implements Pushable {

	public final Pushable owner;
	public final MethodHeader header;
	public final BestList<Pushable> args;
	public final boolean[] paramUse;

	public MethodInvocation(Pushable owner, MethodHeader header, BestList<Pushable> args, boolean[] paramUse) {
		this.owner = owner;
		this.header = header;
		if(args == null)
			this.args = new BestList<>();
		else
			this.args = args;
		if(paramUse == null)
			this.paramUse = new boolean[this.args.size()];
		else
			this.paramUse = paramUse;
	}

	public MethodInvocation(Pushable owner, MethodHeader header) {
		this(owner, header, null, null);
	}

	public MethodInvocation withOwner(Pushable owner) {
		return new MethodInvocation(owner, header, args, paramUse);
	}

	public MethodInvocation withArgs(BestList<Pushable> args) {
		return new MethodInvocation(owner, header, args, paramUse);
	}

	@Override public MethodInvocation push(Actor actor) throws Exception {
		if(owner != null)
			owner.push(actor);
		else if(!header.isStatic())
			throw new IllegalStateException("owner object is null but the header is not static: " + header);
		int arg = 0;
		for(int i = 0; i < paramUse.length; i++) {
			final InternalName argType;
			if(paramUse[i])
				argType = args.get(arg++).push(actor).toInternalName();
			else
				argType = new MethodInvocation(owner, defaultParam(header.params.get(i))).push(actor).toInternalName();
			if(header.paramTypes()[i] == InternalName.STRING)
				CompilationUnit.convertToString(argType, actor);
		}
		header.invoke(actor);
		return this;
	}

	public MethodHeader defaultParam(Param param) {
		return new MethodHeader(header.owner, header.name + "_" + param.name, new ReturnValue(param.toInternalName()), header.access + Opcodes.ACC_SYNTHETIC);
	}

	@Override
	public InternalName pushType(Actor actor) throws Exception {
		return push(actor).toInternalName();
	}

	@Override
	public InternalName toInternalName() {
		return header.returns.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return header.returns.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return header.returns.toBaseType();
	}

}
