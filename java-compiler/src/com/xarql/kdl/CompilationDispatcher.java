package com.xarql.kdl;

import com.xarql.kdl.names.CommonText;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.FileFilter;

public class CompilationDispatcher implements CommonText {

	public static final File DEFAULT_LOC = new File(System.getProperty("user.dir")); // default to current working directory
	public static final FileFilter KDL_FILTER = new RegexFileFilter(".*\\.kdl"); // default to all .kdl files
	public static final boolean DEFAULT_VERBOSE = false; // whether or not to print some extra messages

	public static final String VERBOSE = "verbose";
	public static final String QUIET = "quiet";

	private final File input;
	private final FileFilter filter;
	private final File output;

	public CompilationDispatcher(final File input, final FileFilter filter, final File output) {
		if(input == null)
			this.input = DEFAULT_LOC;
		else
			this.input = input;
		if(filter == null)
			this.filter = KDL_FILTER;
		else
			this.filter = filter;
		this.output = output;
	}

	public CompilationDispatcher(final File input) {
		this(input, KDL_FILTER, null);
	}

	public CompilationDispatcher(final FileFilter filter) {
		this(DEFAULT_LOC, filter, null);
	}

	public CompilationDispatcher() {
		this(DEFAULT_LOC, KDL_FILTER, null);
	}

	public static void main(String[] args) {
		BestList<String> arguments = new BestList<>(args);

		if(arguments.isEmpty())
			new CompilationDispatcher().dispatch();
		else {
			final CompilationDispatcher dispatcher;
			if(arguments.get(0).equalsIgnoreCase(VERBOSE) || arguments.get(0).equalsIgnoreCase(QUIET))
				dispatcher = new CompilationDispatcher(KDL_FILTER);
			else {
				dispatcher = new CompilationDispatcher(new RegexFileFilter(arguments.get(0)));
				arguments.remove(0);
			}

			if(arguments.contains(VERBOSE) && arguments.contains(QUIET))
				throw new IllegalArgumentException("Compilation dispatching can't be both verbose and quiet");
			else if(arguments.contains(VERBOSE))
				dispatcher.dispatchVerbosely();
			else if(arguments.contains(QUIET))
				try {
					dispatcher.dispatchQuietly();
				} catch(Exception e) {
					e.printStackTrace();
					return;
				}
			else
				dispatcher.dispatch();
		}
	}

	public CompilationDispatcher dispatchVerbosely() {
		for(CompilationUnit unit : registerCompilationUnits(input, new BestList<>(), true))
			unit.run();
		return this;
	}

	public CompilationDispatcher dispatch() {
		for(CompilationUnit unit : registerCompilationUnits(input, new BestList<>()))
			unit.run();
		return this;
	}

	public CompilationDispatcher dispatchQuietly() throws Exception {
		for(CompilationUnit unit : registerCompilationUnits(input, new BestList<>()))
			try {
				unit.runSilent();
			} catch(Exception e) {
				throw e;
			}
		return this;
	}

	private BestList<CompilationUnit> registerCompilationUnits(final File f, final BestList<CompilationUnit> units) {
		return registerCompilationUnits(f, units, DEFAULT_VERBOSE);
	}

	private BestList<CompilationUnit> registerCompilationUnits(final File f, final BestList<CompilationUnit> units, final boolean verbose) {
		if(f.isDirectory()) {
			for(File sub : f.listFiles()) {
				registerCompilationUnits(sub, units, verbose);
			}
		} else if(filter.accept(f)) {
			if(verbose)
				System.out.println("Registered " + f.getName());
			units.add(new CompilationUnit(f, output));
		} else if(verbose)
			System.out.println("Skipping file " + f.getName());
		return units;
	}

}
