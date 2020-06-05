package main.com.xarql.kdl;

import jdk.internal.org.objectweb.asm.Opcodes;
import main.com.xarql.kdl.names.InternalObjectName;
import main.com.xarql.kdl.names.ReturnValue;

import java.util.List;

public class MethodDef implements StringOutput, Opcodes {
	public static final MethodDef MAIN = new MethodDef(Type.MTD, "main", new BestList<>(new InternalObjectName(String.class, 1)), null, ACC_PUBLIC + ACC_STATIC);

	public static final int DEFAULT_ACCESS = ACC_STATIC;

	public final Type                     type;
	public final String                   methodName;
	public final ReturnValue              returnValue;
	public final List<InternalObjectName> paramTypes;
	public final int                      access;

	public MethodDef(Type type, String methodName, List<InternalObjectName> paramTypes, ReturnValue returnValue, int access) {
		this.type = type;
		this.methodName = Text.checkNotEmpty(methodName);
		this.paramTypes = Util.nonNullList(paramTypes);
		this.returnValue = ReturnValue.nonNull(returnValue);
		this.access = access;
		if(type == Type.FNC && returnValue.isVoid()) {
			throw new IllegalArgumentException("Pure function (fnc) must return a value");
		}
	}

	public MethodDef(String methodName, boolean isStatic) {
		this(Type.MTD, methodName, null, ReturnValue.VOID_RETURN, DEFAULT_ACCESS + staticAccess(isStatic));
	}

	public MethodDef(Type type, String methodName, ReturnValue returnValue) {
		this(type, methodName, null, returnValue, DEFAULT_ACCESS);
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
		if(obj instanceof MethodDef) {
			MethodDef md = (MethodDef) obj;
			if(obj == this)
				return true;
			else
				return type == md.type && methodName.equals(md.methodName) && returnValue.equals(md.returnValue) && paramTypes.equals(md.paramTypes);
		}
		else
			return false;
	}

	public enum Type {
		FNC("function"), MTD("method");

		String fullName;

		Type(String fullName) {
			this.fullName = fullName;
		}

		public static Type resolve(String type) {
			for(Type t : values())
				if(t.name().equals(type) || t.fullName.equals(type))
					return t;
			throw new IllegalArgumentException("The method type " + type + " is invalid");
		}
	}
}
