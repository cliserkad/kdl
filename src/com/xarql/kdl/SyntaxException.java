package com.xarql.kdl;

public class SyntaxException extends Exception {
    public final String offendingSymbol;
    public final int    line;
    public final int    column;

    public SyntaxException(String offendingSymbol, int line, int column) {
        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.column = column;
    }

    @Override
    public String getMessage() {
        return "Unexpected \"" + offendingSymbol + "\" on line " + line + " at column " + column;
    }

}
