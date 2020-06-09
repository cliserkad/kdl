package com.xarql.kdl;

import com.xarql.kdl.names.CommonNames;
import org.objectweb.asm.Opcodes;

import static com.xarql.kdl.SourceListener.standardHandle;

public class ExpressionHandler implements CommonNames, Opcodes {
	private final SourceListener parent;

	public ExpressionHandler(SourceListener parent) {
		this.parent = parent;
	}

	public static void compute(Value val1, Value val2, Operator opr, LinedMethodVisitor lmv) {
		if(!val1.isBaseType() || !val2.isBaseType()) {
			standardHandle(new UnimplementedException("Custom expressions are not implemented"));
		}
		else {
			switch(val1.toBaseType()) {
				case INT:
				case BOOLEAN:
					computeInt(val2, opr, lmv);
					break;
				case STRING:
					computeString(val2, opr, lmv);
					break;
				default:
					standardHandle(new UnimplementedException(SWITCH_BASETYPE));
			}
		}
	}

	private static void computeString(Value val2, Operator opr, LinedMethodVisitor lmv) {

	}

	private static void computeInt(Value val2, Operator opr, LinedMethodVisitor lmv) {
		if(val2.toBaseType() == STRING)
			standardHandle(new IncompatibleTypeException("strings can not be modifiers to ints"));
			// under the hood booleans should be either 0 or 1
		else if(val2.toBaseType() == INT || val2.toBaseType() == BOOLEAN) {
			switch(opr) {
				case PLUS:
					lmv.visitInsn(IADD);
					break;
				case MINUS:
					lmv.visitInsn(ISUB);
					break;
				case MULTIPLY:
					lmv.visitInsn(IMUL);
					break;
				case DIVIDE:
					lmv.visitInsn(IDIV);
					break;
				case MODULUS:
					lmv.visitInsn(IREM);
					break;
				default:
					standardHandle(new UnimplementedException(SWITCH_OPERAOTR));
			}
		}
	}
}
