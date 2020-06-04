package main.com.xarql.kdl;

import java.util.ArrayList;
import java.util.List;

public class Source {
    public final String   className;
    public List<Constant> constants;

    public Source(String className) {
        this.className = className;
        constants = new ArrayList<>();
    }

}
