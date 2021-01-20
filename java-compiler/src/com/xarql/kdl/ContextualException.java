package com.xarql.kdl;

import org.antlr.v4.runtime.ParserRuleContext;

public class ContextualException extends Exception {

	public final ParserRuleContext ctx;
	public final Exception reason;

	public ContextualException(ParserRuleContext ctx, Exception reason) {
		this.ctx = ctx;
		this.reason = reason;
	}

	@Override
	public String getMessage() {
		return "At " + ctx.getStart().getLine() + ":" + ctx.getStart().getCharPositionInLine() + " | " + innerMessage();
	}

	public String innerMessage() {
		if(reason.getMessage() == null)
			return ExceptionPack.NO_MSG;
		else
			return reason.getMessage();
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return reason.getStackTrace();
	}

}
