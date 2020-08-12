package com.xarql.kdl;

import com.xarql.kdl.names.CommonNames;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.FileFilter;

public class CompilationDispatcher implements CommonNames {
	// location of source directory
	public static final File       DEFAULT_LOC = new File(System.getProperty("user.dir"));
	public static final FileFilter KDL_FILTER  = new RegexFileFilter(".*\\.kdl");
	// whether or not to print some extra messages
	public static final boolean    VERBOSE     = false;

	private final File       input;
	private final FileFilter filter;

	public CompilationDispatcher(final File input, final FileFilter filter) {
		if(input == null)
			this.input = DEFAULT_LOC;
		else
			this.input = input;
		if(filter == null)
			this.filter = KDL_FILTER;
		else
			this.filter = filter;
	}

	public CompilationDispatcher() {
		this(DEFAULT_LOC, KDL_FILTER);
	}

	public static void main(String[] args) {
		if(args.length < 1)
			new CompilationDispatcher().compileAll();
		else
			new CompilationDispatcher(DEFAULT_LOC, new RegexFileFilter(args[0])).compileAll();
	}

	public CompilationDispatcher compileAll() {
		for(CompilationUnit unit : registerCompilationUnits(input, new BestList<>(), VERBOSE))
			unit.run();
		return this;
	}

	private BestList<CompilationUnit> registerCompilationUnits(File f, BestList<CompilationUnit> units, boolean verbose) {
		if(f.isDirectory()) {
			for(File sub : f.listFiles()) {
				registerCompilationUnits(sub, units, verbose);
			}
		}
		else if(filter.accept(f)) {
			if(verbose)
				System.out.println("Registered " + f.getName());
			units.add(new CompilationUnit(f));
		}
		else if(verbose)
			System.out.println("Skipping file " + f.getName());
		return units;
	}

}
