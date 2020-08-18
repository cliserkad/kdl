package com.xarql.kdl;

import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.calculable.*;
import com.xarql.kdl.names.ToName;
import org.objectweb.asm.MethodVisitor;

public class Range {
    public static final int DEFAULT_MIN = 0;

    public final Calculable min;
    public final Calculable max;

    public Range(kdl.RangeContext ctx, CompilationUnit unit, MethodVisitor visitor) throws Exception {
        if(ctx.expression().size() > 1)
            min = new Expression(ctx.expression(0), unit);
        else
            min = new Literal<Integer>(DEFAULT_MIN);

        max = new Expression(ctx.expression(ctx.expression().size() - 1), unit);
    }
}
