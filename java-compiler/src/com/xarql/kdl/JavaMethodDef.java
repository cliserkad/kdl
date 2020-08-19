package com.xarql.kdl;

import com.xarql.kdl.names.CommonText;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;
import com.xarql.kdl.names.ReturnValue;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Method;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.internalName;

public class JavaMethodDef implements StringOutput, CommonText {
	public static final JavaMethodDef MAIN      = new JavaMethodDef(internalName(Object.class), "main", list(new InternalObjectName(String.class, 1)), VOID, ACC_PUBLIC + ACC_STATIC);
	public static final JavaMethodDef TO_STRING = new JavaMethodDef(internalName(Object.class), "toString", null, ReturnValue.STRING_RETURN, ACC_PUBLIC);

	public static final String INIT           = "<init>";
	public static final int    DEFAULT_ACCESS = ACC_PUBLIC + ACC_STATIC;

	public final InternalName owner;
	public final String                   methodName;
	public final BestList<InternalObjectName> paramTypes;
	public final ReturnValue returnValue;
	public final int                      access;

	public JavaMethodDef(InternalName owner, String methodName, BestList<InternalObjectName> paramTypes, ReturnValue returnValue, int access) {
		this.owner = owner; // TODO: add check against primitives
		this.methodName = Text.checkNotEmpty(methodName);
		if(paramTypes == null)
			this.paramTypes = new BestList<InternalObjectName>();
		else
			this.paramTypes = paramTypes;
		this.returnValue = ReturnValue.nonNull(returnValue);
		this.access = access;
	}

	public JavaMethodDef(Class<?> jvmClass, Method method) {
		this.owner = InternalName.internalName(jvmClass);
		this.methodName = method.getName();
		this.paramTypes = new BestList<>();
		if(method.getParameterTypes().length > 0) {
			for(Class<?> c : method.getParameterTypes())
				paramTypes.add(new InternalObjectName(c));
		}
		this.returnValue = ReturnValue.returnValue(method.getReturnType());
		this.access = method.getModifiers();
	}

	public JavaMethodDef withOwner(CustomClass cc) {
		return new JavaMethodDef(new InternalName(cc), methodName, paramTypes, returnValue, access);
	}

	public JavaMethodDef withOwner(InternalName owner) {
		return new JavaMethodDef(owner, methodName, paramTypes, returnValue, access);
	}

	public JavaMethodDef withAccess(final int access) {
		return new JavaMethodDef(owner, methodName, paramTypes, returnValue, access);
	}

	public static void main(String[] args) {
		System.out.println(MAIN);
	}

	public static int staticAccess(boolean isStatic) {
		if(isStatic)
			return ACC_STATIC;
		else
			return 0;
	}

	public String descriptor() {
		String out = "";
		out += "(";
		for(InternalObjectName ion : paramTypes)
			out += ion.stringOutput();
		out += ")";
		out += returnValue.stringOutput();
		return out;
	}

	public String owner() {
		return owner.stringOutput();
	}

	@Override
	public String stringOutput() {
		return descriptor();
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
			else
				return methodName.equals(md.methodName) && returnValue.equals(md.returnValue) && paramTypes.equals(md.paramTypes);
		}
		else
			return false;
	}

	public JavaMethodDef resolve(CompilationUnit src) throws SymbolResolutionException {
		return resolveAgainst(src.getMethods());
	}

	public JavaMethodDef resolveAgainst(BestList<JavaMethodDef> methods) throws SymbolResolutionException {
		for(JavaMethodDef def : methods) {
			if (owner.equals(def.owner) && methodName.equals(def.methodName) && paramsCompatible(def.paramTypes))
				return def;
		}
		throw new SymbolResolutionException("Couldn't resolve given method " + this);
	}

	public boolean paramsCompatible(BestList<InternalObjectName> others) {
		if(paramTypes.size() != others.size())
			return false;
		for(int i = 0; i < paramTypes.size(); i++) {
			if(!paramTypes.get(i).compatibleWith(others.get(i)))
				return false;
		}
		return true;
	}

	private JavaMethodDef invoke0(final int type, final MethodVisitor visitor) {
		visitor.visitMethodInsn(type, owner.stringOutput(), methodName, descriptor(), false);
		return this;
	}

	public JavaMethodDef invoke(MethodVisitor visitor) {
		if((access & ACC_STATIC) == ACC_STATIC)
			return invoke0(INVOKESTATIC, visitor);
		else if((access & ACC_PRIVATE) == ACC_PRIVATE || methodName.equals(INIT))
			return invoke0(INVOKESPECIAL, visitor);
		else
			return invoke0(INVOKEVIRTUAL, visitor);
	}

}
