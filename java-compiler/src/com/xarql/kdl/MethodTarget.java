package com.xarql.kdl;

import com.xarql.kdl.names.InternalName;

import java.util.Set;

public class MethodTarget {

	public final InternalName owner;
	public final String name;
	public final BestList<InternalName> args;
	public final boolean requireStatic;

	public MethodTarget(InternalName owner, String name, BestList<InternalName> args, boolean requireStatic) {
		this.owner = owner;
		this.name = name;
		if(args == null)
			this.args = new BestList<>();
		else
			this.args = args;
		this.requireStatic = requireStatic;
	}

	public MethodInvocation resolve(Actor actor) throws SymbolResolutionException {
		return resolveAgainst(actor.unit.methods());
	}

	public MethodInvocation resolveAgainst(Set<MethodHeader> methods) throws SymbolResolutionException {
		for(MethodHeader def : methods) {
			if(owner.equals(def.owner) && name.equals(def.name) && staticCompatible(def)) {
				final boolean[] usage = paramsCompatible(def);
				if(usage != null)
					return new MethodInvocation(null, def, null, usage);
			}
		}
		throw new SymbolResolutionException("Couldn't resolve given method " + this);
	}

	public boolean staticCompatible(MethodHeader header) {
		if(requireStatic) {
			return header.isStatic();
		} else {
			return true;
		}
	}

	public boolean[] paramsCompatible(MethodHeader header) {
		if(header.params.size() < args.size())
			return null;

		boolean[] usage = new boolean[header.params.size()];
		int arg = 0;
		for(int dest = 0; dest < usage.length; dest++) {
			if(args.size() > dest && args.get(arg).compatibleWith(header.paramTypes()[dest])) { // if the argument is compatible with the parameter, go to the next arg
				arg++;
				usage[dest] = true;
			} else if(!header.availableDefaults()[dest]) // if the parameter was necessary, but incompatible
				return null;
		}

		return usage;
	}

}
