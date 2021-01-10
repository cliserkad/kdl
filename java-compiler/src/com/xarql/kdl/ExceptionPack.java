package com.xarql.kdl;

import java.util.List;

public class ExceptionPack extends Exception {
	public static final String NO_MSG = "No message provided";

	private final Exception[] exceptions;

	public ExceptionPack(List<Exception> exceptions) {
		this.exceptions = exceptions.toArray(new Exception[exceptions.size()]);
	}

	public ExceptionPack(Exception[] exceptions) {
		this.exceptions = exceptions;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\n");
		for(Exception e : exceptions) {
			builder.append('\t');
			if(e instanceof ContextualException)
				builder.append(((ContextualException) e).reason.getClass().getCanonicalName());
			else
				builder.append(e.getClass().getCanonicalName());
			builder.append(" >> ");
			if(e.getMessage() != null)
				builder.append(e.getMessage().replace("\n", " | "));
			else
				builder.append(NO_MSG);
			builder.append("\n");
			builder.append(stackTrace(e.getStackTrace()));
			builder.append("\n");
		}
		builder.append("}");
		return builder.toString();
	}

	public static String stackTrace(StackTraceElement[] elements) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for(StackTraceElement ste : elements) {
			builder.append("\t\tat ");
			builder.append(ste.toString());
			i++;
			if(i < 6)
				builder.append("\n");
			else
				break;
		}
		return builder.toString();
	}

	public Exception[] getExceptions() {
		Exception[] out = new Exception[exceptions.length];
		System.arraycopy(exceptions, 0, out,0, exceptions.length);
		return out;
	}

	@Override
	public String toString() {
		return "ExceptionPack with " + exceptions.length + " elements " + getMessage();
	}

}
