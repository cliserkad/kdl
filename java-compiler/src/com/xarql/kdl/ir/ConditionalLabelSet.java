package com.xarql.kdl.ir;

import org.objectweb.asm.Label;

public class ConditionalLabelSet {
	public final Label check;
	public final Label onTrue;
	public final Label onFalse;
	public final Label exit;

	public ConditionalLabelSet() {
		check = new Label();
		onTrue = new Label();
		onFalse = new Label();
		exit = new Label();
	}
}
