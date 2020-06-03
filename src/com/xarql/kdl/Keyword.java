package com.xarql.kdl;

public enum Keyword {
    CLASS(), PRINT(), CONST(), INT(), STRING();

    public static boolean matches(String in) {
        for(Keyword k : Keyword.values())
            if(in.equalsIgnoreCase(k.name()))
                return true;
        return false;
    }
}
