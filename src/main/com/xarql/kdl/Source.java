package main.com.xarql.kdl;

import java.util.ArrayList;
import java.util.List;

public class Source {
    public final String   className;
    public BestList<Constant> constants;
    public MethodDef mainMethodDef;

    public Source(String className) {
        this.className = className;
        constants = new BestList<>();
    }

}
