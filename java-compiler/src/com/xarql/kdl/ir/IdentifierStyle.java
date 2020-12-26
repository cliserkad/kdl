package com.xarql.kdl.ir;

import com.xarql.kdl.Text;

public enum IdentifierStyle {
    CONSTANT(), VAR(), TYPE(), IMPROPER();

    public static final String PROPER_REGEX = "[a-zA-Z0-9_]*";
    public static final String CONST_REGEX = "[A-Z0-9_]*";

    public static IdentifierStyle match(String name) {
        if(!isProper(name))
            return IMPROPER;
        else if(name.matches(CONST_REGEX))
            return CONSTANT;
        else if(Text.isFirstLetterUppercase(name))
            return TYPE;
        else
            return VAR;
    }

    public static boolean isProper(String name) {
        return name.matches(PROPER_REGEX);
    }
}
