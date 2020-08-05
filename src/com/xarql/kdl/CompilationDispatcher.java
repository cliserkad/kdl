package com.xarql.kdl;

import com.xarql.kdl.names.CommonNames;
import org.objectweb.asm.*;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompilationDispatcher implements CommonNames {
	// location of source directory
	public static final File    DEFAULT_LOC = new File(System.getProperty("user.home") + "/IdeaProjects/kdl/src/com/xarql/kdl/test");
	// whether or not to print some extra messages
	public static final boolean VERBOSE     = false;

	private final File input;

	public CompilationDispatcher(final File input) {
		this.input = input;
	}

	public static void main(String[] args) {
		new CompilationDispatcher(DEFAULT_LOC).compileAll();
	}

	public void compileAll() {
		for(CompilationUnit unit : registerCompilationUnits(input, new BestList<CompilationUnit>(), VERBOSE))
			unit.run();
	}

	private static BestList<CompilationUnit> registerCompilationUnits(File f, BestList<CompilationUnit> units, boolean verbose) {
		if(f.isDirectory()) {
			for(File sub : f.listFiles()) {
				registerCompilationUnits(sub, units, verbose);
			}
		}
		else if(f.getName().endsWith(".kdl")) {
			if(verbose)
				System.out.println("Registered " + f.getName());
			units.add(new CompilationUnit(f));
		}
		else if(verbose)
			System.out.println("Skipping file " + f.getName());
		return units;
	}

}
