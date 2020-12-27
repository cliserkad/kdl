package com.xarql.kdl;

import com.xarql.kdl.ir.Variable;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.Details;
import com.xarql.kdl.names.ReturnValue;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Scope implements Opcodes {

	public final String name;
	private final BestList<Variable> variables;
	private int index = 0;

	private final Label start;
	private final Label end;

	public Scope(final String name, final MethodVisitor visitor) {
		this.name = name;
		variables = new BestList<>();
		start = new Label();
		visitor.visitLabel(start);
		end = new Label();
	}

	public Label getEnd() {
		return end;
	}

	public Label getStart() {
		return start;
	}

	public Variable newVar(final String name, final ToName type, final boolean mutable) {
		Variable var = addLocalVariable(new Variable(name, type.toInternalName(), nextIndex(), mutable));
		// increment it again to reserve a second slot if its a 64-bit number
		if(type.toBaseType() == BaseType.LONG || type.toBaseType() == BaseType.DOUBLE)
			index++;
		return var;
	}

	public Variable newVar(final String name, final ToName type) {
		return newVar(name, type, Variable.DEFAULT_MUTABLE);
	}

	public Variable newVar(final Details details) {
		return newVar(details.name.text, details.type, details.mutable);
	}

	public Variable addLocalVariable(Variable lv) {
		if(!variables.contains(lv))
			variables.add(lv);
		else
			throw new IllegalArgumentException("The variable " + lv + "already exists within this scope");
		return lv;
	}

	public Label end(final int line, final MethodVisitor visitor, final ReturnValue rv) {
		final Label ret = new Label();
		visitor.visitLabel(ret);
		visitor.visitLineNumber(line, ret);
		if(rv.isVoid())
			visitor.visitInsn(RETURN);
		else
			visitor.visitInsn(NOP);

		visitor.visitLabel(end);
		for(Variable lv : all())
			visitor.visitLocalVariable(lv.name.text, lv.type.toString(), null, start, end, lv.localIndex);
		visitor.visitMaxs(0, 0);
		visitor.visitEnd();

		return ret;
	}

	public boolean contains(String varname) {
		for(Variable var : variables)
			if(var.name.equals(varname))
				return true;
		return false;
	}

	public Variable get(String name) throws SymbolResolutionException {
		for(Variable lv : variables)
			if(lv.name.equals(name))
				return lv;
		throw new SymbolResolutionException("The variable \"" + name + "\" does not exist in " + this);
	}

	public int nextIndex() {
		return index++;
	}

	public BestList<Variable> all() {
		BestList<Variable> out = new BestList<>();
		for(Variable lv : variables)
			out.add(lv);
		return out;
	}

	@Override
	public String toString() {
		return "Scope: " + name;
	}

}
