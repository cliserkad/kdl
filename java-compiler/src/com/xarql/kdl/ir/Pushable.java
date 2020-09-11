package com.xarql.kdl.ir;

import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

/**
 * Represents anything that may be pushed on to the JVM stack.
 * Resolvables are a type of Calculable that do not require any
 * instructions to be executed after pushing. After the push method
 * is invoked, only 1 value should be added to the stack.
 */
public interface Pushable extends ToName {
	/**
	 * Pushes this value on to the stack. Executes sub-pushes and instructions if needed.
	 * Use this over pushType() whenever an InternalName is not required.
	 * @param visitor any MethodVisitor
	 * @return instance of implementing class; whatever "this" is
	 * @throws Exception if pushing is impossible
	 */
	public Pushable push(final MethodVisitor visitor) throws Exception;

	/**
	 * Pushes this value on to the stack. Returns the type of the value.
	 * Should call push().
	 * @param visitor any MethodVisitor
	 * @return pushed value type
	 * @throws Exception if pushing is impossible
	 * @see Pushable#push(MethodVisitor)
	 */
	public InternalName pushType(final MethodVisitor visitor) throws Exception;

	/**
	 * Attempts to parse a Resolvable symbol
	 * @param unit The CompilationUnit in which the symbol appears
	 * @param val  The symbol
	 * @return A Resolvable whose actual type corresponds to the symbol
	 * @throws UnimplementedException thrown if missing a symbol from the grammar
	 */
	public static Pushable parse(final CompilationUnit unit, final kdl.ValueContext val) throws Exception {
		if(val.literal() != null)
			return Literal.parseLiteral(val.literal());
		else if(val.CONSTNAME() != null)
			return unit.getConstant(val.CONSTNAME().getText());
		else if(val.VARNAME() != null)
			return unit.getLocalVariable(val.VARNAME().getText());
		else if(val.indexAccess() != null)
			return new IndexAccess(unit.getLocalVariable(val.indexAccess().VARNAME().getText()), new Expression(val.indexAccess().expression(), unit));
		else if(val.subSequence() != null)
			return new SubSequence(val.subSequence(), unit);
		else if(val.arrayLength() != null)
			return new ArrayLength(unit.getLocalVariable(val.arrayLength().VARNAME().getText()));
		else if(val.R_NULL() != null)
			return new Null();
		else if(val.methodCall() != null)
			return new MethodCall(val.methodCall(), unit);
		else if(val.newObject() != null)
			return new NewObject(val.newObject(), unit);
		else
			throw new UnimplementedException("a type of Resolvable wasn't parsed correctly\n The input text was \"" + val.getText() + "\"");
	}
}
