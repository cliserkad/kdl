package com.xarql.kdl;

import com.xarql.kdl.calculable.Variable;
import org.objectweb.asm.Label;

public class Scope {
	public final  String             name;
	private final BestList<Variable> variables;

	private final Label start;
	private Label end;

	public Scope(final String name, final Label start) {
		this.name = name;
		variables = new BestList<>();
		this.start = start;
	}

	public Scope(final String name) {
		this(name, new Label());
	}

	public Label getEnd() {
		return end;
	}

	public Label getStart() {
		return start;
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
