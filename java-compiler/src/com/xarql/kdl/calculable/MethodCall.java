package com.xarql.kdl.calculable;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.*;

public class MethodCall implements CommonNames, Resolvable {
    public final JavaMethodDef method;
    private final BestList<Calculable> arguments;

    public MethodCall(kdl.MethodCallContext ctx, CompilationUnit unit) throws Exception {
        final String methodName = ctx.VARNAME().getText();

        final BestList<InternalObjectName> params;
        arguments = new BestList<>();
        if(ctx.parameterSet() != null && ctx.parameterSet().expression().size() > 0) {
            params = new BestList<>();
            for(kdl.ExpressionContext xpr : ctx.parameterSet().expression()) {
                Expression xpr1 = new Expression(xpr, unit);
                params.add(xpr1.toInternalObjectName());
                arguments.add(xpr1);
            }
        }
        else
            params = new BestList<>();

        JavaMethodDef known = new JavaMethodDef(new InternalName(unit.getClazz()), methodName, params, null, ACC_PUBLIC + ACC_STATIC);
        method = known.resolve(unit);
    }

    @Override
    public Resolvable push(LinedMethodVisitor lmv) throws Exception {
        calc(lmv);
        return this;
    }

    @Override
    public ToName calc(LinedMethodVisitor lmv) throws Exception {
        for(int i = 0; i < arguments.size(); i++) {
            arguments.get(i).calc(lmv);
            if(method.paramTypes.get(i) == STRING_ION) {
                CompilationUnit.convertToString(arguments.get(i).toInternalObjectName(), lmv);
            }
        }
        method.invokeStatic(lmv);
        return this;
    }

    @Override
    public InternalName toInternalName() {
        if(method.returnValue.returnType == null)
            return new InternalName();
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
        String out = "\n\tCalculable --> {";
        for(Calculable arg : arguments)
            out += "\n\t\t" + arg.toString().replace("\n", "\n\t\t");
        out += "\n\t}";
        return out;
    }

}
