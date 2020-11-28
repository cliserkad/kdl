package com.xarql.kdl;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

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
		final StringBuilder builder = new StringBuilder();
		builder.append("Encountered syntax errors while parsing within ");
		if(unit.isFromFile())
			builder.append(unit.sourcePath());
		else
			builder.append(unit.unitName());
		builder.append("\n");
		for(SyntaxException e : errors) {
			builder.append("\t");
			builder.append(e.getMessage());
			builder.append("\n");
		}
		System.err.print(builder.toString());
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		errors.add(new SyntaxException(msg, line, charPositionInLine));
	}

}
