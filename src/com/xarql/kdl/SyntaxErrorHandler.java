package com.xarql.kdl;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.util.List;

public class SyntaxErrorHandler extends BaseErrorListener {
    public final BestList<SyntaxException> errors;
    public final CompilationUnit unit;

    public SyntaxErrorHandler(CompilationUnit unit) {
        errors = new BestList<>();
        this.unit = unit;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void printErrors() {
        for(SyntaxException e : errors)
            System.err.println("Within " + unit.unitName() + ": " + e.getMessage());
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        errors.add(new SyntaxException(e.getOffendingToken().getText(), line, charPositionInLine));
    }
}
