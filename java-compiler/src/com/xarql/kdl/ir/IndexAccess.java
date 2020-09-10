package com.xarql.kdl.ir;

import com.xarql.kdl.IncompatibleTypeException;
import com.xarql.kdl.JavaMethodDef;
import com.xarql.kdl.UnimplementedException;
import com.xarql.kdl.names.*;
import org.objectweb.asm.MethodVisitor;

import static com.xarql.kdl.BestList.list;
import static com.xarql.kdl.names.BaseType.INT;

/**
 * Represents the access of an array's element
 */
public class IndexAccess implements CommonText, Resolvable {
	public static final JavaMethodDef STRING_CHAR_AT = new JavaMethodDef(InternalName.STRING, "charAt", list(BaseType.INT.toInternalName()), ReturnValue.CHAR, ACC_PUBLIC);

	public final Variable   variable;
	public final Calculable index;

	public IndexAccess(final Variable variable, final Calculable index) {
		this.variable = variable;
		this.index = index;
	}

	@Override
	public Resolvable push(MethodVisitor visitor) throws Exception {
		calc(visitor);
		return this;
	}

	@Override
	public ToName calc(MethodVisitor visitor) throws Exception {
		visitor.visitVarInsn(ALOAD, variable.localIndex);
		// throw error if value within [ ] isn't an int
		if(index.toBaseType().ordinal() > INT.ordinal())
			throw new IncompatibleTypeException("The input for an array access must be an integer");
		else
			index.calc(visitor);

		if(variable.isArray()) {
			if(variable.type.isBaseType()) {
				switch(variable.type.toBaseType()) {
					case INT:
					case BOOLEAN:
						visitor.visitInsn(IALOAD);
						break;
					case STRING:
						visitor.visitInsn(AALOAD);
						break;
					default:
						throw new UnimplementedException(SWITCH_BASETYPE);
				}
			}
			else
				visitor.visitInsn(AALOAD);

			return variable.type.withoutArray();
		}
		else if(variable.toBaseType() == BaseType.STRING && !variable.isArray()) {
			STRING_CHAR_AT.invoke(visitor);
			return BaseType.CHAR;
		}
		else
			throw new IllegalArgumentException(variable + " is not an array nor a string");
	}

	@Override
	public InternalName toInternalName() {
		return variable.toInternalName();
	}

	@Override
	public boolean isBaseType() {
		return variable.isBaseType();
	}

	@Override
	public BaseType toBaseType() {
		return variable.toBaseType();
	}

	@Override
	public String toString() {
		return "ArrayAccess --> {\n\t" + variable + "\n\t" + index + "\n}";
	}
}
