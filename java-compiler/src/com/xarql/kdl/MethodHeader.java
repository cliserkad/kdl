package com.xarql.kdl;

import com.xarql.kdl.ir.Member;
import com.xarql.kdl.ir.Param;
import com.xarql.kdl.ir.Pushable;
import com.xarql.kdl.names.*;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.util.List;

import static com.xarql.kdl.names.BaseType.*;

public class MethodHeader implements CommonText, ToType, Member {

	public static final MethodHeader MAIN = new MethodHeader(Type.get(Object.class), "main", toParamList(new TypeDescriptor(String.class, 1)), TypeDescriptor.VOID, ACC_PUBLIC + ACC_STATIC);
	public static final MethodHeader TO_STRING = new MethodHeader(Type.get(Object.class), "toString", null, STRING.toTypeDescriptor(), ACC_PUBLIC);
	public static final MethodHeader INIT = new MethodHeader(Type.get(Object.class), "<init>", null, TypeDescriptor.VOID, ACC_PUBLIC);
	public static final MethodHeader STATIC_INIT = new MethodHeader(Type.get(Object.class), "<clinit>", null, TypeDescriptor.VOID, ACC_PUBLIC + ACC_STATIC + ACC_FINAL);
	public static final MethodHeader EQUALS = new MethodHeader(Type.get(Object.class), "equals", toParamList(new TypeDescriptor(Object.class)), BOOLEAN.toTypeDescriptor(), ACC_PUBLIC);

	public static final String S_INIT = "<init>";
	public static final String S_STATIC_INIT = "<clinit>";
	public static final int DEFAULT_ACCESS = ACC_PUBLIC + ACC_STATIC;

	public final Type owner;
	public final String name;
	public final BestList<Param> params;
	public final TypeDescriptor yield;
	public final int access;

	public MethodHeader(ToType owner, String name, BestList<Param> params, TypeDescriptor yield, int access) {
		this.owner = owner.toType(); // TODO: add check against primitives
		this.name = Text.checkNotEmpty(name);

		// check paramTypes
		if(params == null)
			this.params = new BestList<>();
		else {
			for(Param param : params)
				if(param == null || param.descriptor == null)
					throw new NullPointerException("A parameter or argument's type may not be null");
			this.params = params;
		}

		this.yield = yield;
		this.access = access;
	}

	public MethodHeader(ToType owner, String name, TypeDescriptor yield, int access) {
		this(owner, name, null, yield, access);
	}

	public MethodHeader(ToType owner, String name, int access) {
		this(owner, name, null, null, access);
	}

	public MethodHeader(ToType owner, Method method) {
		this.owner = owner.toType();
		this.name = method.getName();
		this.params = new BestList<>();
		if(method.getParameterTypes().length > 0) {
			int i = 0;
			for(Class<?> c : method.getParameterTypes()) {
				if(c == null)
					throw new NullPointerException();
				params.add(new Param(new Details("param" + i, new TypeDescriptor(c), true), null));
				i++;
			}
		}
		if(method.getReturnType().equals(void.class))
			this.yield = TypeDescriptor.VOID;
		else
			this.yield = new TypeDescriptor(method.getReturnType());
		this.access = method.getModifiers();
	}

	public MethodHeader withOwner(final Type type) {
		return new MethodHeader(type, name, params, yield, access);
	}

	public MethodHeader withAccess(final int access) {
		return new MethodHeader(owner, name, params, yield, access);
	}

	public MethodHeader withReturnValue(final ToTypeDescriptor returnValue) {
		return new MethodHeader(owner, name, params, returnValue.toTypeDescriptor(), access);
	}

	public static void main(String[] args) {
		System.out.println(MAIN);
	}

	public String descriptor() {
		String out = "";
		out += "(";
		for(TypeDescriptor in : paramTypes())
			out += in.arrayName();
		out += ")";
		out += yield.arrayName();
		return out;
	}

	public String owner() {
		return owner.toTypeDescriptor().qualifiedName();
	}

	public boolean[] availableDefaults() {
		boolean[] out = new boolean[params.size()];
		for(int i = 0; i < params.size(); i++)
			out[i] = params.get(i).defaultValue != null;
		return out;
	}

	public static BestList<Param> toParamList(ToTypeDescriptor...types) {
		if(types == null)
			return null;
		else {
			return toParamList(new BestList<>(types));
		}
	}

	public static BestList<Param> toParamList(List<ToTypeDescriptor> types) {
		final BestList<Param> params = new BestList<>();
		for(int i = 0; i < types.size(); i++)
			params.add(new Param(new Details("unknown" + i, types.get(i).toTypeDescriptor(), true), null));
		return params;
	}

	public TypeDescriptor[] paramTypes() {
		final TypeDescriptor[] types = new TypeDescriptor[params.size()];
		for(int i = 0; i < params.size(); i++)
			types[i] = params.get(i).toTypeDescriptor();
		return types;
	}

	@Override
	public String toString() {
		if(descriptor() != null)
			return owner() + "." + name + descriptor();
		else
			return owner() + "." + name;
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
				return name.equals(md.name) && yield.equals(md.yield) && owner.equals(md.owner);
			}
		} else
			return false;
	}

	private MethodHeader invoke(final int type, final MethodVisitor visitor) {
		visitor.visitMethodInsn(type, owner(), name, descriptor(), false);
		return this;
	}

	public boolean isStatic() {
		return (access & ACC_STATIC) == ACC_STATIC;
	}

	@Override public Type toType() {
		return yield.toType();
	}

	@Override public boolean isBaseType() {
		return yield.isBaseType();
	}

	@Override public BaseType toBaseType() {
		return yield.toBaseType();
	}

	@Override
	public Details details() {
		// methods are not modifiable at runtime
		return new Details(name, toTypeDescriptor(), false);
	}

	@Override
	public Pushable push(Actor actor) throws Exception {
		if((access & ACC_STATIC) == ACC_STATIC)
			return invoke(INVOKESTATIC, actor);
		else if((access & ACC_PRIVATE) == ACC_PRIVATE || name.equals(S_INIT))
			return invoke(INVOKESPECIAL, actor);
		else
			return invoke(INVOKEVIRTUAL, actor);
	}

	@Override
	public TypeDescriptor toTypeDescriptor() {
		return yield;
	}
}
