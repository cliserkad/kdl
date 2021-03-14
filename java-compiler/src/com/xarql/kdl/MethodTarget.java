package com.xarql.kdl;

import com.xarql.kdl.names.TypeDescriptor;

import java.util.Set;

public class MethodTarget {

	public final Type owner;
	public final String name;
	public final BestList<TypeDescriptor> args;
	public final boolean requireStatic;

	public MethodTarget(Type owner, String name, BestList<TypeDescriptor> args, boolean requireStatic) {
		this.owner = owner;
		this.name = name;
		if(args == null)
			this.args = new BestList<>();
		else
			this.args = args;
		this.requireStatic = requireStatic;
	}

	public MethodInvocation resolve(Actor actor) throws SymbolResolutionException {
		try {
			return resolveAgainst(actor.unit.type.getMethods());
		} catch(SymbolResolutionException sre) {
			throw new SymbolResolutionException(sre.getMessage());
		}
	}

	public MethodInvocation resolveAgainst(Set<MethodHeader> methods) throws SymbolResolutionException {
		for(MethodHeader def : methods) {
			if(owner.equals(def.owner) && name.equals(def.name) && staticCompatible(def)) {
				final boolean[] usage = paramsCompatible(def);
				if(usage != null)
					return new MethodInvocation(null, def, null, usage);
			}
		}
		StringBuilder builder = new StringBuilder();
		for(MethodHeader def : methods) {
			builder.append(def);
			builder.append("\n");
		}
		throw new SymbolResolutionException("Couldn't resolve given method " + this + ". Methods available:\n" + builder.toString());
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

	@Override
	public String toString() {
		final String prepend;
		if(requireStatic)
			prepend = "static";
		else
			prepend = "instance";
		return prepend + " " + owner + "." + name + "(" + args.toString() + ")";
	}

}
