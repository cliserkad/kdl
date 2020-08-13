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
	public static final boolean    DEFAULT_SILENT = false;

	private final File       input;
	private final FileFilter filter;
	private final boolean    silent;

	public CompilationDispatcher(final File input, final FileFilter filter, final boolean silent) {
		if(input == null)
			this.input = DEFAULT_LOC;
		else
			this.input = input;
		if(filter == null)
			this.filter = KDL_FILTER;
		else
			this.filter = filter;
		this.silent = silent;
	}

	public CompilationDispatcher(final File input, final FileFilter filter) {
		this(input, filter, DEFAULT_SILENT);
	}

	public CompilationDispatcher(final File input, final boolean silent) {
		this(input, KDL_FILTER, DEFAULT_SILENT);
	}

	public CompilationDispatcher(final FileFilter filter, final boolean silent) {
		this(DEFAULT_LOC, filter, silent);
	}

	public CompilationDispatcher(final File input) {
		this(input, KDL_FILTER, DEFAULT_SILENT);
	}

	public CompilationDispatcher(final FileFilter filter) {
		this(DEFAULT_LOC, filter, DEFAULT_SILENT);
	}

	public CompilationDispatcher() {
		this(DEFAULT_LOC, KDL_FILTER, DEFAULT_SILENT);
	}

	public static void main(String[] args) {
		if(args.length < 1)
			new CompilationDispatcher().compileAll();
		else
			new CompilationDispatcher(DEFAULT_LOC, new RegexFileFilter(args[0])).compileAll();
	}

	public CompilationDispatcher compileAll() {
		for(CompilationUnit unit : registerCompilationUnits(input, new BestList<>(), VERBOSE)) {
			if(silent)
				unit.runSilent();
			else
				unit.run();
		}
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
