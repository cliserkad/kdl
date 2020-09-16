package com.xarql.kdl;

import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ReturnValue;

public class MethodDef extends JavaMethodDef {

	public static final Type DEFAULT_TYPE = Type.MTD;
	public static final int DEFAULT_ACCESS = ACC_PUBLIC;

	public final Type type;

	/**
	 * Creates a .kdl MethodDef
	 * 
	 * @param type        compiletime restrictions
	 * @param methodName  name of method
	 * @param paramTypes  the type of every parameter
	 * @param returnValue the type that is returned
	 * @param access      access modifiers
	 */
	public MethodDef(InternalName owner, Type type, String methodName, BestList<InternalName> paramTypes, ReturnValue returnValue, int access) {
		super(owner, methodName, paramTypes, returnValue, access);
		this.type = type;
	}

	public MethodDef(JavaMethodDef parent, Type type) {
		this(parent.owner, type, parent.methodName, parent.paramTypes, parent.returnValue, parent.access);
	}

	/**
	 * Creates a method with methodName that is public, void, and has no parameters
	 * 
	 * @param methodName
	 */
	public MethodDef(InternalName owner, String methodName) {
		this(owner, DEFAULT_TYPE, methodName, null, VOID, DEFAULT_ACCESS);
	}

	/**
	 * Used to track compiletime restrictions of .kdl methods
	 */
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
