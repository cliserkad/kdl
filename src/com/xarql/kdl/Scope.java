package com.xarql.kdl;

import com.xarql.kdl.calculable.Variable;
import org.objectweb.asm.Label;

public class Scope {
	public final  String             name;
	private final BestList<Variable> variables;

	private Label start;
	private Label end;

	public Scope(String name, Label start) {
		this.name = name;
		variables = new BestList<>();
	}

	public Label getEnd() {
		return end;
	}

	public void setEnd(Label end) {
		if(this.end == null)
			this.end = end;
	}

	public Label getStart() {
		return start;
	}

	public void setStart(Label start) {
		if(this.start == null)
			this.start = start;
	}

	public Variable addLocalVariable(Variable lv) {
		if(!variables.contains(lv))
			variables.add(lv);
		return lv;
	}

	public Variable getVariable(String name) {
		for(Variable lv : variables)
			if(lv.name.equals(name))
				return lv;
		throw new IllegalArgumentException("The variable with name " + name + " does not exist in current scope");
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
