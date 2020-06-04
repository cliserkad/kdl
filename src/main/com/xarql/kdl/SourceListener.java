package main.com.xarql.kdl;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.pattern.ParseTreeMatch;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.antlr.v4.runtime.tree.xpath.XPath;

import java.util.Collection;
import java.util.List;

public class SourceListener extends kdlBaseListener {
    private Builder owner;

    public SourceListener(Builder owner) {
         this.owner = owner;
    }

    private boolean lookingForConstLiteral = false;

    @Override
    public void enterClazz(kdlParser.ClazzContext clazzCtx) {
        owner.src = new Source(clazzCtx.getText());
    }

    @Override
    public void enterLiteral(kdlParser.LiteralContext ctx) {
        if(ctx.getParent() instanceof kdlParser.ConstantContext) {
            kdlParser.ConstantContext parent = (kdlParser.ConstantContext) ctx.getParent();
            Constant c = new Constant(parent.getChild(1).toString());
            if(ctx.STRING() != null)
                c.value = new StringValue(ctx.STRING().toString());
            else if(ctx.bool() != null) {
                if(ctx.bool().TRUE() != null)
                    c.value = new BooleanValue(true);
                else
                    c.value = new BooleanValue(false);
            }
            else
                System.out.println("FUCK IT DIDN'T WORK");
            owner.src.constants.add(c);
        }
    }

    @Override
    public void enterRun(kdlParser.RunContext ctx) {
       for(kdlParser.StatementContext state : ctx.statement()){

        }
    }

}
