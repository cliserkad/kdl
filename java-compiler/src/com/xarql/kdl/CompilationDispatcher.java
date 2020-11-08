package com.xarql.kdl;

import com.xarql.kdl.names.CommonText;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.Objects;

public class CompilationDispatcher implements CommonText {

	public static final File DEFAULT_INPUT = new File(System.getProperty("user.dir")); // default to current working directory
	public static final File DEFUALT_OUTPUT = new File(System.getProperty("user.dir"), "/target/classes/");

	public static final FileFilter KDL_FILTER = new RegexFileFilter(".*\\.kdl"); // default to all .kdl files

	public static final String QUIET = "quiet";
	public static final boolean DEFAULT_QUIET = false;

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
			if(arguments.get(0).equalsIgnoreCase(QUIET))
				dispatcher = new CompilationDispatcher(null, KDL_FILTER, null);
			else {
				dispatcher = new CompilationDispatcher(null, new RegexFileFilter(arguments.get(0)), null);
				arguments.remove(0);
			}

			if(arguments.contains(QUIET))
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

	public CompilationDispatcher dispatch() {
		printDirs();
		compile(registerCompilationUnits());
		return this;
	}

	public CompilationDispatcher dispatchQuietly() throws Exception {
		final BestList<CompilationUnit> units = registerCompilationUnits();
		for(int pass = 0; pass < CompilationUnit.PASSES; pass++) {
			for(CompilationUnit unit : units) {
				unit.pass(true);
			}
		}
		return this;
	}

	public void printDirs() {
		System.out.println("Input Directory: " + input);
		System.out.println("Output Directory: " + output);
	}

	private void compile(final BestList<CompilationUnit> units) {
		for(int pass = 0; pass < CompilationUnit.PASSES; pass++) {
			for(CompilationUnit unit : units) {
				try {
					unit.pass();
				} catch(Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	public BestList<CompilationUnit> registerCompilationUnits() {
		return registerCompilationUnits(input, new BestList<>());
	}

	public BestList<CompilationUnit> registerCompilationUnits(final File f, final BestList<CompilationUnit> units) {
		if(f.isDirectory()) {
			for(File sub : Objects.requireNonNull(f.listFiles())) {
				registerCompilationUnits(sub, units);
			}
		} else if(filter.accept(f))
			units.add(new CompilationUnit(f, output));
		return units;
	}

}
