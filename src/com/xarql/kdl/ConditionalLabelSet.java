package com.xarql.kdl;

import org.objectweb.asm.Label;

public class ConditionalLabelSet {
	public final Label intro;
	public final Label onTrue;
	public final Label onFalse;
	public final Label exit;

	public ConditionalLabelSet() {
		intro = new Label();
		onTrue = new Label();
		onFalse = new Label();
		exit = new Label();
	}
}
