package com.xarql.kdl;

import static com.xarql.kdl.BestList.list;
import java.lang.reflect.Method;
import org.objectweb.asm.MethodVisitor;
import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;

public class JavaMethodDef implements CommonText {

	public static final JavaMethodDef MAIN = new JavaMethodDef(new InternalName(Object.class), "main", list(new InternalName(String.class, 1)), VOID, ACC_PUBLIC + ACC_STATIC);
	public static final JavaMethodDef TO_STRING = new JavaMethodDef(new InternalName(Object.class), "toString", null, ReturnValue.STRING, ACC_PUBLIC);
	public static final JavaMethodDef INIT = new JavaMethodDef(new InternalName(Object.class), "<init>", null, ReturnValue.VOID, ACC_PUBLIC);
	public static final JavaMethodDef STATIC_INIT = new JavaMethodDef(new InternalName(Object.class), "<clinit>", null, ReturnValue.VOID, ACC_PUBLIC + ACC_STATIC + ACC_FINAL);

	public static final String S_INIT = "<init>";
	public static final String S_STATIC_INIT = "<clinit>";
	public static final int DEFAULT_ACCESS = ACC_PUBLIC + ACC_STATIC;

	public final InternalName owner;
	public final String methodName;
	public final BestList<InternalName> paramTypes;
	public final ReturnValue returnValue;
	public final int access;

	public JavaMethodDef(InternalName owner, String methodName, BestList<InternalName> paramTypes, ReturnValue returnValue, int access) {
		this.owner = owner; // TODO: add check against primitives
		this.methodName = Text.checkNotEmpty(methodName);
		if(paramTypes == null)
			this.paramTypes = new BestList<>();
		else
			this.paramTypes = paramTypes;
		this.returnValue = ReturnValue.nonNull(returnValue);
		this.access = access;
	}

	public JavaMethodDef(Class<?> jvmClass, Method method) {
		this.owner = new InternalName(jvmClass);
		this.methodName = method.getName();
		this.paramTypes = new BestList<>();
		if(method.getParameterTypes().length > 0) {
			for(Class<?> c : method.getParameterTypes()) {
				paramTypes.add(new InternalName(c));
			}
		}
		this.returnValue = new ReturnValue(method.getReturnType());
		this.access = method.getModifiers();
	}

	public JavaMethodDef withOwner(final CustomClass cc) {
		return new JavaMethodDef(new InternalName(cc), methodName, paramTypes, returnValue, access);
	}

	public JavaMethodDef withOwner(final InternalName owner) {
		return new JavaMethodDef(owner, methodName, paramTypes, returnValue, access);
	}

	public JavaMethodDef withAccess(final int access) {
		return new JavaMethodDef(owner, methodName, paramTypes, returnValue, access);
	}

	public JavaMethodDef withReturnValue(final ReturnValue returnValue) {
		return new JavaMethodDef(owner, methodName, paramTypes, returnValue, access);
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
			return owner + "." + methodName + descriptor();
		else
			return owner + "." + methodName;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof JavaMethodDef) {
			JavaMethodDef md = (JavaMethodDef) obj;
			if(obj == this)
				return true;
			else if(md.owner == null)
				return methodName.equals(md.methodName) && returnValue.equals(md.returnValue) && paramTypes.equals(md.paramTypes);
			else
				return methodName.equals(md.methodName) && returnValue.equals(md.returnValue) && paramTypes.equals(md.paramTypes) && owner.equals(md.owner);
		} else
			return false;
	}

	public JavaMethodDef resolve(CompilationUnit src) throws SymbolResolutionException {
		return resolveAgainst(src.getMethods());
	}

	public JavaMethodDef resolveAgainst(BestList<JavaMethodDef> methods) throws SymbolResolutionException {
		for(JavaMethodDef def : methods) {
			if(owner.equals(def.owner) && methodName.equals(def.methodName) && paramsCompatible(def.paramTypes))
				return def;
		}
		throw new SymbolResolutionException("Couldn't resolve given method " + this);
	}

	public boolean paramsCompatible(BestList<InternalName> others) {
		if(paramTypes.size() != others.size())
			return false;
		for(int i = 0; i < paramTypes.size(); i++) {
			if(!paramTypes.get(i).compatibleWith(others.get(i)))
				return false;
		}
		return true;
	}

	private JavaMethodDef invoke0(final int type, final MethodVisitor visitor) {
		visitor.visitMethodInsn(type, owner.nameString(), methodName, descriptor(), false);
		return this;
	}

	public JavaMethodDef invoke(MethodVisitor visitor) {
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
