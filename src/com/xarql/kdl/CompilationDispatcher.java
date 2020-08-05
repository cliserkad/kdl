package com.xarql.kdl;

import org.objectweb.asm.*;
import java.io.File;

public class CompilationDispatcher implements Opcodes {
	public static final File DEFAULT_LOC = new File(System.getProperty("user.home") + "/IdeaProjects/kdl/src/com/xarql/kdl/test");

	private final File input;

	public CompilationDispatcher(final File input) {
		this.input = input;
	}

	public static void main(String[] args) {
		new CompilationDispatcher(DEFAULT_LOC).compileAll(Boolean.parseBoolean(args[0]));
		System.out.println("Done!");
	}

	public void compileAll(boolean verbose) {
		dispatch(input, verbose);
	}

	private static void dispatch(File f, boolean verbose) {
		if(f.isDirectory()) {
			for(File sub : f.listFiles()) {
				dispatch(sub, verbose);
			}
		}
		else if(f.getName().endsWith(".kdl")) {
			System.out.println("Compiling " + f.getName());
			Thread compileThread = new Thread(new CompilationUnit(f));
			compileThread.start();
		}
		else if(verbose) {
			System.out.println("Skipping file " + f.getName());
		}
	}

}
