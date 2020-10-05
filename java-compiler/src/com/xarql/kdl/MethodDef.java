package com.xarql.kdl;

import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;
import java.util.Set;

import static com.xarql.kdl.BestList.list;

public class MethodDef implements CommonText {

	public static final MethodDef MAIN = new MethodDef(new InternalName(Object.class), "main", list(new InternalName(String.class, 1)), VOID, ACC_PUBLIC + ACC_STATIC);
	public static final MethodDef TO_STRING = new MethodDef(new InternalName(Object.class), "toString", null, ReturnValue.STRING, ACC_PUBLIC);
	public static final MethodDef INIT = new MethodDef(new InternalName(Object.class), "<init>", null, ReturnValue.VOID, ACC_PUBLIC);
	public static final MethodDef STATIC_INIT = new MethodDef(new InternalName(Object.class), "<clinit>", null, ReturnValue.VOID, ACC_PUBLIC + ACC_STATIC + ACC_FINAL);
	public static final MethodDef EQUALS = new MethodDef(new InternalName(Object.class), "equals", list(new InternalName(Object.class)), ReturnValue.BOOLEAN, ACC_PUBLIC);

	public static final String S_INIT = "<init>";
	public static final String S_STATIC_INIT = "<clinit>";
	public static final int DEFAULT_ACCESS = ACC_PUBLIC + ACC_STATIC;
	public static final boolean[] DEFAULT_DEFAULTS = new boolean[]{};

	public final InternalName owner;
	public final String methodName;
	public final BestList<InternalName> paramTypes;
	public final ReturnValue returnValue;
	public final int access;
	public final boolean[] defaults; // tracks which params are necessary

	public MethodDef(InternalName owner, String methodName, BestList<InternalName> paramTypes, ReturnValue returnValue, int access, boolean[] defaults) {
		boolean[] defaults1;
		this.owner = owner; // TODO: add check against primitives
		this.methodName = Text.checkNotEmpty(methodName);

		// check paramTypes
		if(paramTypes == null)
			this.paramTypes = new BestList<>();
		else {
			for(InternalName pt : paramTypes)
				if(pt == null)
					throw new NullPointerException("A parameter or argument's type may not be null");
			this.paramTypes = paramTypes;
		}

		this.returnValue = ReturnValue.nonNull(returnValue);
		this.access = access;
		this.defaults = defaults;
	}

	public MethodDef(InternalName owner, String methodName, BestList<InternalName> paramTypes, ReturnValue returnValue, int access) {
		this(owner, methodName, paramTypes, returnValue, access, DEFAULT_DEFAULTS);
	}

	public MethodDef(Class<?> jvmClass, Method method) {
		this.owner = new InternalName(jvmClass);
		this.methodName = method.getName();
		this.paramTypes = new BestList<>();
		if(method.getParameterTypes().length > 0) {
			for(Class<?> c : method.getParameterTypes()) {
				if(c == null)
					throw new NullPointerException();
				paramTypes.add(new InternalName(c));
			}
		}
		if(method.getReturnType().equals(void.class))
			this.returnValue = ReturnValue.VOID;
		else
			this.returnValue = new ReturnValue(method.getReturnType());
		this.defaults = DEFAULT_DEFAULTS;
		this.access = method.getModifiers();
	}

	public MethodDef withOwner(final CustomClass cc) {
		return new MethodDef(new InternalName(cc), methodName, paramTypes, returnValue, access);
	}

	public MethodDef withOwner(final InternalName owner) {
		return new MethodDef(owner, methodName, paramTypes, returnValue, access);
	}

	public MethodDef withAccess(final int access) {
		return new MethodDef(owner, methodName, paramTypes, returnValue, access);
	}

	public MethodDef withReturnValue(final ReturnValue returnValue) {
		return new MethodDef(owner, methodName, paramTypes, returnValue, access);
	}

	public static void main(String[] args) {
		System.out.println(MAIN);
	}

	public String descriptor() {
		String out = "";
		out += "(";
		for(InternalName in : paramTypes)
			out += in.objectString();
		out += ")";
		out += returnValue.stringOutput();
		return out;
	}

	public String owner() {
		return owner.nameString();
	}

	@Override
	public String toString() {
		if(descriptor() != null)
			return owner.nameString() + "." + methodName + descriptor();
		else
			return owner.nameString() + "." + methodName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MethodDef) {
			MethodDef md = (MethodDef) obj;

			if(obj == this)
				return true;
			else if(paramTypes.size() != md.paramTypes.size())
				return false;
			else {
				for(int i = 0; i < md.paramTypes.size(); i++)
					if(!paramTypes.get(i).equals(md.paramTypes.get(i)))
						return false;
				return methodName.equals(md.methodName) && returnValue.equals(md.returnValue) && owner.equals(md.owner);
			}
		} else
			return false;
	}

	public MethodDef resolve(CompilationUnit src) throws SymbolResolutionException {
		return resolveAgainst(src.getMethods());
	}

	public MethodDef resolveAgainst(Set<MethodDef> methods) throws SymbolResolutionException {
		for(MethodDef def : methods) {
			if(owner.equals(def.owner) && methodName.equals(def.methodName) && paramsCompatible(def))
				return def;
		}
		throw new SymbolResolutionException("Couldn't resolve given method " + this);
	}

	public boolean paramsCompatible(MethodDef other) {
		if(paramTypes.size() < other.paramTypes.size())
			return false;

		int dest = 0;
		for(int arg = 0; arg < paramTypes.size(); ) {
			if(paramTypes.get(arg).compatibleWith(other.paramTypes.get(dest))) // if the argument is compatible with the parameter, go to the next arg
				arg++;
			else if(other.defaults[dest]) // if the parameter was necessary, but incompatible
				return false;
			dest++;
		}

		return true;
	}

	private MethodDef invoke0(final int type, final MethodVisitor visitor) {
		visitor.visitMethodInsn(type, owner.nameString(), methodName, descriptor(), false);
		return this;
	}

	public MethodDef invoke(MethodVisitor visitor) {
		if((access & ACC_STATIC) == ACC_STATIC)
			return invoke0(INVOKESTATIC, visitor);
		else if((access & ACC_PRIVATE) == ACC_PRIVATE || methodName.equals(S_INIT))
			return invoke0(INVOKESPECIAL, visitor);
		else
			return invoke0(INVOKEVIRTUAL, visitor);
	}

	public boolean isStatic() {
		return (access & ACC_STATIC) == ACC_STATIC;
	}

}
