package com.xarql.kdl.calculable;

import com.xarql.kdl.*;
import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.names.*;
import org.objectweb.asm.MethodVisitor;

public class MethodCall implements CommonText, Resolvable {
    public final JavaMethodDef method;
    private final BestList<Calculable> arguments;

    public MethodCall(kdl.MethodCallStatementContext ctx, CompilationUnit unit) throws Exception {

        // parse methodCall alone
        final String methodName = ctx.methodCall().VARNAME().getText();
        arguments = parseArguments(ctx.methodCall().parameterSet(), unit);
        final BestList<InternalObjectName> params = new BestList<>();
        for(Calculable arg : arguments)
            params.add(arg.toInternalObjectName());

        // determine which class owns the method being called
        final InternalName owner;
        int accessModifier = 0;
        if(ctx.CLASSNAME() != null) {
            owner = unit.resolveAgainstImports(ctx.CLASSNAME().getText());
            accessModifier += ACC_STATIC;
        }
        else if(ctx.VARNAME() != null)
            owner = unit.getLocalVariable(ctx.VARNAME().getText()).toInternalName();
        else {
            owner = new InternalName(unit.getClazz());
            accessModifier += ACC_STATIC;
        }

        JavaMethodDef known = new JavaMethodDef(owner, methodName, params, null, ACC_PUBLIC + accessModifier);
        method = known.resolve(unit);
    }

    public MethodCall(kdl.MethodCallContext ctx, CompilationUnit unit) throws Exception {
        final String methodName = ctx.VARNAME().getText();
        arguments = parseArguments(ctx.parameterSet(), unit);
        final BestList<InternalObjectName> params = new BestList<>();
        for(Calculable arg : arguments)
            params.add(arg.toInternalObjectName());

        JavaMethodDef known = new JavaMethodDef(new InternalName(unit.getClazz()), methodName, params, null, ACC_PUBLIC + ACC_STATIC);
        method = known.resolve(unit);
    }

    public static BestList<Calculable> parseArguments(kdl.ParameterSetContext ctx, CompilationUnit unit) throws Exception {
        final BestList<Calculable> arguments = new BestList<>();
        if(ctx != null && ctx.expression().size() > 0) {
            for(kdl.ExpressionContext xpr : ctx.expression()) {
                Expression xpr1 = new Expression(xpr, unit);
                arguments.add(xpr1);
            }
        }
        return arguments;
    }

    @Override
    public Resolvable push(final MethodVisitor visitor) throws Exception {
        calc(visitor);
        return this;
    }

    @Override
    public ToName calc(final MethodVisitor visitor) throws Exception {
        for(int i = 0; i < arguments.size(); i++) {
            arguments.get(i).calc(visitor);
            if(method.paramTypes.get(i) == InternalObjectName.STRING) {
                CompilationUnit.convertToString(arguments.get(i).toInternalObjectName(), visitor);
            }
        }
        method.invoke(visitor);
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
