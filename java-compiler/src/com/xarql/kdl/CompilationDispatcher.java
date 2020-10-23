package com.xarql.kdl;

import com.xarql.kdl.names.CommonText;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.FileFilter;

public class CompilationDispatcher implements CommonText {

	public static final File DEFAULT_INPUT = new File(System.getProperty("user.dir")); // default to current working directory
	public static final File DEFUALT_OUTPUT = new File(System.getProperty("user.dir"), "/target/classes/");

	public static final FileFilter KDL_FILTER = new RegexFileFilter(".*\\.kdl"); // default to all .kdl files
	public static final boolean DEFAULT_VERBOSE = false; // whether or not to print some extra messages

	public static final String VERBOSE = "verbose";
	public static final String QUIET = "quiet";

	private final File input;
	private final FileFilter filter;
	private final File output;

	public CompilationDispatcher(final File input, final FileFilter filter, final File output) {
		if(input == null)
			this.input = DEFAULT_INPUT;
		else
			this.input = input;
		if(filter == null)
			this.filter = KDL_FILTER;
		else
			this.filter = filter;
		if(output == null)
			this.output = DEFUALT_OUTPUT;
		else
			this.output = output;
	}

	public static void main(String[] args) {
		BestList<String> arguments = new BestList<>(args);

		if(arguments.isEmpty())
			new CompilationDispatcher(null, null, null).dispatch();
		else {
			final CompilationDispatcher dispatcher;
			if(arguments.get(0).equalsIgnoreCase(VERBOSE) || arguments.get(0).equalsIgnoreCase(QUIET))
				dispatcher = new CompilationDispatcher(null, KDL_FILTER, null);
			else {
				dispatcher = new CompilationDispatcher(null, new RegexFileFilter(arguments.get(0)), null);
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
		System.out.println("Input Directory: " + input);
		System.out.println("Output Directory: " + output);
		for(CompilationUnit unit : registerCompilationUnits(true))
			unit.run();
		return this;
	}

	public CompilationDispatcher dispatch() {
		System.out.println("Input Directory: " + input);
		System.out.println("Output Directory: " + output);
		for(CompilationUnit unit : registerCompilationUnits())
			unit.run();
		return this;
	}

	public CompilationDispatcher dispatchQuietly() throws Exception {
		for(CompilationUnit unit : registerCompilationUnits())
			try {
				unit.runSilent();
			} catch(Exception e) {
				throw e;
			}
		return this;
	}

	public BestList<CompilationUnit> registerCompilationUnits() {
		return registerCompilationUnits(DEFAULT_VERBOSE);
	}

	public BestList<CompilationUnit> registerCompilationUnits(final boolean verbose) {
		return registerCompilationUnits(input, new BestList<CompilationUnit>(), verbose);
	}

	public BestList<CompilationUnit> registerCompilationUnits(final File f, final BestList<CompilationUnit> units, final boolean verbose) {
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
