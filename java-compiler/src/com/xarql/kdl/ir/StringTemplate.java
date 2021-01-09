package com.xarql.kdl.ir;

import com.xarql.kdl.Actor;
import com.xarql.kdl.CompilationUnit;
import com.xarql.kdl.Type;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class StringTemplate implements Pushable {

	private final List<Pushable> elements;

	public StringTemplate() {
		elements = new ArrayList<>();
	}

	public void add(String s) {
		elements.add(new Literal<>(s));
	}

	public void add(Pushable pushable) {
		elements.add(pushable);
	}

	public boolean isTextOnly() {
		for(Pushable p : elements)
			if(!(p instanceof Literal<?>))
				return false;
		return true;
	}

	@Override
	public Type toType() {
		return BaseType.STRING.toType();
	}

	@Override
	public boolean isBaseType() {
		return true;
	}

	@Override
	public BaseType toBaseType() {
		return BaseType.STRING;
	}

	@Override
	public Pushable push(final Actor actor) throws Exception {
		Expression.createStringBuilder(actor);
		for(Pushable p : elements) {
			if(p instanceof Constant)
				p = actor.unit.getConstant(((Constant) p).name.text);
			TypeDescriptor type = p.push(actor).toTypeDescriptor();
			if(!type.equals(BaseType.STRING.toTypeDescriptor()))
				CompilationUnit.convertToString(type, actor);
			Expression.SB_APPEND.push(actor);
		}
		Expression.SB_TO_STRING.push(actor);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for(Pushable p : elements) {
			if(p instanceof Literal<?>) {
				Literal<?> lit = (Literal<?>) p;
				builder.append(lit.value);
			} else
				builder.append(p);
		}
		return builder.toString();
	}

	@Override
	public TypeDescriptor toTypeDescriptor() {
		return BaseType.STRING.toTypeDescriptor();
	}
}
