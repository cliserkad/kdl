package com.xarql.kdl;

import com.xarql.kdl.calculable.Variable;
import com.xarql.kdl.names.InternalObjectName;
import com.xarql.kdl.names.ReturnValue;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class Scope implements Opcodes {
	public final  String             name;
	private final BestList<Variable> variables;

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

	public Variable newVariable(final String name, final ToName type) {
		return addLocalVariable(new Variable(name, type.toInternalObjectName(), nextIndex()));
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
		for(Variable lv : getVariables())
			visitor.visitLocalVariable(lv.name, lv.type.toString(), null, start, end, lv.localIndex);
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

	public Variable getVariable(String name) {
		for(Variable lv : variables)
			if(lv.name.equals(name))
				return lv;
		throw new IllegalArgumentException("The variable with name " + name + " does not exist in " + this);
	}

	public int nextIndex() {
		return variables.size();
	}

	public BestList<Variable> getVariables() {
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
