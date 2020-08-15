package com.xarql.kdl.calculable;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr4.kdlParser;
import com.xarql.kdl.names.BaseType;
import com.xarql.kdl.names.CommonNames;
import com.xarql.kdl.names.InternalName;
import com.xarql.kdl.names.InternalObjectName;

import javax.management.remote.JMXServerErrorException;

public class MethodCall extends DefaultResolvable implements CommonNames {
    public final JavaMethodDef method;
    private final BestList<Resolvable> arguments;

    public MethodCall(kdlParser.MethodCallContext ctx, CompilationUnit unit) throws Exception {
        final String methodName = ctx.VARNAME().getText();

        final BestList<InternalObjectName> params;
        arguments = new BestList<>();
        if(ctx.parameterSet() != null && ctx.parameterSet().expression().size() > 0) {
            params = new BestList<>();
            for(com.xarql.kdl.antlr4.kdlParser.ExpressionContext xpr : ctx.parameterSet().expression()) {
                Resolvable res = Resolvable.parse(unit, xpr.value(0));
                params.add(res.toInternalObjectName());
                arguments.add(res);
            }
        }
        else
            params = null;

        JavaMethodDef known = new JavaMethodDef(new InternalName(unit.getClazz()), methodName, params, null, ACC_PUBLIC + ACC_STATIC);
        method = known.resolve(unit);
    }

    @Override
    /**
     * Executes the method and pushes the return value on to the stack
     */
    public Resolvable push(LinedMethodVisitor lmv) throws Exception {
        for(int i = 0; i < arguments.size(); i++) {
            arguments.get(i).push(lmv);
            if(method.paramTypes.get(i) == STRING_ION) {
                CompilationUnit.convertToString(arguments.get(i).toInternalObjectName(), lmv);
            }
        }
        method.invokeStatic(lmv);
        return this;
    }

    @Override
    public InternalName toInternalName() {
        return method.returnValue.returnType.toInternalName();
    }

    @Override
    public InternalObjectName toInternalObjectName() {
        return method.returnValue.returnType.toInternalObjectName();
    }

    @Override
    public boolean isBaseType() {
        return method.returnValue.returnType.isBaseType();
    }

    @Override
    public BaseType toBaseType() {
        return method.returnValue.returnType.toBaseType();
    }

    @Override
    public String toString() {
        return "MethodCall --> {\n\tMethod --> " + method + arguments() + "\n}";
    }

    private String arguments() {
        String out = "\n\tResolvables --> {";
        for(Resolvable arg : arguments)
            out += "\n\t\t" + arg.toString().replace("\n", "\n\t\t");
        out += "\n\t}";
        return out;
    }
}
