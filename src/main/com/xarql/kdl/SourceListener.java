package main.com.xarql.kdl;

public class SourceListener extends kdlBaseListener {
    private Source src;

    private boolean lookingForConstLiteral = false;

    @Override
    public void enterClazz(kdlParser.ClazzContext clazzCtx) {
        src = new Source(clazzCtx.getText());
    }

    @Override
    public void enterConstant(kdlParser.ConstantContext ctx) {
        src.constants.add(new Constant(ctx.CONSTNAME().getText()));
        lookingForConstLiteral = true;
    }

    @Override
    public void enterBool(kdlParser.BoolContext ctx) {
        if(lookingForConstLiteral) {
            latestConstant().value = new BooleanValue(Boolean.valueOf(ctx.getText()));
            lookingForConstLiteral = false;
        }
    }

    @Override
    public void enterString(kdlParser.StringContext ctx) {
        if(lookingForConstLiteral) {
            latestConstant().value = new StringValue(ctx.getText().substring(0, ctx.getText().length() - 2));
            lookingForConstLiteral = false;
        }
    }

    private Constant latestConstant() {
        return src.constants.get(src.constants.size() - 1);
    }

}
