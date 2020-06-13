package com.xarql.kdl;

import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;

import javax.xml.transform.Source;

public class ConditionalHandler implements CommonNames, Opcodes {
	private final SourceListener owner;

	public ConditionalHandler(SourceListener owner) {
		this.owner = owner;
	}

	public void handle(kdlParser.ConditionalContext ctx, LinedMethodVisitor lmv) {
		if (ctx.r_if() != null) {

			Label trueLabel = new Label();
			Label endLabel = new Label();

			final kdlParser.ConditionContext cnd = ctx.r_if().condition();


			// assuming both are literal booleans
			final Value a = owner.pushValue(cnd.value(0), lmv);
			// if the condition has two values
			if(cnd.value(1) != null) {
				final Comparator cmp = Comparator.match(cnd.comparator().getText());
				final Value b = owner.pushValue(cnd.value(1), lmv);
				if(a.toBaseType() == BOOLEAN && b.toBaseType() == BOOLEAN) {
					switch(cmp) {
						case EQUAL:
							lmv.visitJumpInsn(IF_ICMPEQ, trueLabel);
							break;
						case NOT_EQUAL:
							lmv.visitJumpInsn(IF_ICMPNE, trueLabel);
							break;
						default:
							SourceListener.standardHandle(new IncompatibleTypeException("Two booleans may not be compared with " + cmp));
					}
				}
				else
					SourceListener.standardHandle(new UnimplementedException("Conditions are not complete"));
			}
			else if (a.isBaseType()) {
				switch(a.toBaseType()) {
					case BOOLEAN:
					case INT:
						lmv.visitJumpInsn(IFGT, trueLabel);
						break;
					case STRING:
						lmv.visitJumpInsn(IFNONNULL, trueLabel);
						break;
				}
			}
			else
				SourceListener.standardHandle(new IncompatibleTypeException("Don't know how to handle a ref type without a comparator"));

			if (ctx.r_if().r_else() != null)
				owner.consumeStatementSet(ctx.r_if().r_else().statementSet(), lmv);
			lmv.visitJumpInsn(GOTO, endLabel);

			lmv.visitLabel(trueLabel);
			owner.consumeStatementSet(ctx.r_if().statementSet(), lmv);
			lmv.visitLabel(endLabel);
		}
	}
}
