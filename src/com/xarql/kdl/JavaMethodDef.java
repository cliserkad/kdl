package com.xarql.kdl;

import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;
import com.xarql.kdl.names.ReturnValue;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.util.List;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.InternalName.internalName;

public class JavaMethodDef implements StringOutput, Opcodes, CommonNames {
	public static final JavaMethodDef MAIN      = new JavaMethodDef(internalName(Object.class), "main", list(new InternalObjectName(String.class, 1)), null, ACC_PUBLIC + ACC_STATIC);
	public static final JavaMethodDef TO_STRING = new JavaMethodDef(internalName(Object.class), "toString", null, STRING_RV, ACC_PUBLIC);

	public static final String INIT           = "<init>";
	public static final int    DEFAULT_ACCESS = ACC_STATIC;

	public final InternalName             owner;
	public final String                   methodName;
	public final List<InternalObjectName> paramTypes;
	public final ReturnValue              returnValue;
	public final int                      access;

	public JavaMethodDef(InternalName owner, String methodName, List<InternalObjectName> paramTypes, ReturnValue returnValue, int access) {
		this.owner = owner; // TODO: add check against primitives
		this.methodName = Text.checkNotEmpty(methodName);
		this.paramTypes = Util.nonNullList(paramTypes);
		this.returnValue = ReturnValue.nonNull(returnValue);
		this.access = access;
	}

	public JavaMethodDef withOwner(ClassCreator cc) {
		return new JavaMethodDef(new InternalName(cc), methodName, paramTypes, returnValue, access);
	}

	public JavaMethodDef withOwner(InternalName owner) {
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
		return stringOutput();
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


}
