package com.xarql.kdl;

import com.xarql.kdl.ir.Pushable;
import com.xarql.kdl.names.*;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;

import static com.xarql.kdl.BestList.list;

public class MethodHeader implements CommonText, ToName {

	public static final MethodHeader MAIN = new MethodHeader(new InternalName(Object.class), "main", list(new InternalName(String.class, 1)), VOID, ACC_PUBLIC + ACC_STATIC);
	public static final MethodHeader TO_STRING = new MethodHeader(new InternalName(Object.class), "toString", null, ReturnValue.STRING, ACC_PUBLIC);
	public static final MethodHeader INIT = new MethodHeader(new InternalName(Object.class), "<init>", null, ReturnValue.VOID, ACC_PUBLIC);
	public static final MethodHeader STATIC_INIT = new MethodHeader(new InternalName(Object.class), "<clinit>", null, ReturnValue.VOID, ACC_PUBLIC + ACC_STATIC + ACC_FINAL);
	public static final MethodHeader EQUALS = new MethodHeader(new InternalName(Object.class), "equals", list(new InternalName(Object.class)), ReturnValue.BOOLEAN, ACC_PUBLIC);

	public static final String S_INIT = "<init>";
	public static final String S_STATIC_INIT = "<clinit>";
	public static final int DEFAULT_ACCESS = ACC_PUBLIC + ACC_STATIC;

	public final InternalName owner;
	public final String name;
	public final BestList<InternalName> params;
	public final BestList<Pushable> defaults;
	public final ReturnValue returns;
	public final int access;

	public MethodHeader(InternalName owner, String name, BestList<InternalName> params, ReturnValue returns, int access, BestList<Pushable> defaults) {
		boolean[] defaults1;
		this.owner = owner; // TODO: add check against primitives
		this.name = Text.checkNotEmpty(name);

		// check paramTypes
		if(params == null)
			this.params = new BestList<>();
		else {
			for(InternalName pt : params)
				if(pt == null)
					throw new NullPointerException("A parameter or argument's type may not be null");
			this.params = params;
		}

		this.returns = ReturnValue.nonNull(returns);
		this.access = access;
		if(defaults == null)
			this.defaults = new BestList<Pushable>();
		else
			this.defaults = defaults;
	}

	public MethodHeader(InternalName owner, String name, BestList<InternalName> params, ReturnValue returns, int access) {
		this(owner, name, params, returns, access, null);
	}

	public MethodHeader(Class<?> jvmClass, Method method) {
		this.owner = new InternalName(jvmClass);
		this.name = method.getName();
		this.params = new BestList<>();
		if(method.getParameterTypes().length > 0) {
			for(Class<?> c : method.getParameterTypes()) {
				if(c == null)
					throw new NullPointerException();
				params.add(new InternalName(c));
			}
		}
		if(method.getReturnType().equals(void.class))
			this.returns = ReturnValue.VOID;
		else
			this.returns = new ReturnValue(method.getReturnType());
		this.defaults = new BestList<>();
		this.access = method.getModifiers();
	}

	public MethodHeader withOwner(final CustomClass cc) {
		return new MethodHeader(new InternalName(cc), name, params, returns, access);
	}

	public MethodHeader withOwner(final InternalName owner) {
		return new MethodHeader(owner, name, params, returns, access);
	}

	public MethodHeader withAccess(final int access) {
		return new MethodHeader(owner, name, params, returns, access);
	}

	public MethodHeader withReturnValue(final ReturnValue returnValue) {
		return new MethodHeader(owner, name, params, returnValue, access);
	}

	public static void main(String[] args) {
		System.out.println(MAIN);
	}

	public String descriptor() {
		String out = "";
		out += "(";
		for(InternalName in : params)
			out += in.objectString();
		out += ")";
		out += returns.stringOutput();
		return out;
	}

	public String owner() {
		return owner.nameString();
	}

	public boolean[] availableDefaults() {
		boolean[] out = new boolean[defaults.size()];
		for(int i = 0; i < defaults.size(); i++)
			out[i] = defaults.get(i) != null;
		return out;
	}

	@Override
	public String toString() {
		if(descriptor() != null)
			return owner.nameString() + "." + name + descriptor();
		else
			return owner.nameString() + "." + name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MethodHeader) {
			MethodHeader md = (MethodHeader) obj;

			if(obj == this)
				return true;
			else if(params.size() != md.params.size())
				return false;
			else {
				for(int i = 0; i < md.params.size(); i++)
					if(!params.get(i).equals(md.params.get(i)))
						return false;
				return name.equals(md.name) && returns.equals(md.returns) && owner.equals(md.owner);
			}
		} else
			return false;
	}

	private MethodHeader invoke0(final int type, final MethodVisitor visitor) {
		visitor.visitMethodInsn(type, owner.nameString(), name, descriptor(), false);
		return this;
	}

	public MethodHeader invoke(MethodVisitor visitor) {
		if((access & ACC_STATIC) == ACC_STATIC)
			return invoke0(INVOKESTATIC, visitor);
		else if((access & ACC_PRIVATE) == ACC_PRIVATE || name.equals(S_INIT))
			return invoke0(INVOKESPECIAL, visitor);
		else
			return invoke0(INVOKEVIRTUAL, visitor);
	}

	public boolean isStatic() {
		return (access & ACC_STATIC) == ACC_STATIC;
	}

	@Override public InternalName toInternalName() {
		return returns.toInternalName();
	}

	@Override public boolean isBaseType() {
		return returns.isBaseType();
	}

	@Override public BaseType toBaseType() {
		return returns.toBaseType();
	}
}
