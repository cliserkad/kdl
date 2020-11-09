package com.xarql.kdl;

import com.xarql.kdl.antlr.kdl;
import com.xarql.kdl.ir.Constant;
import com.xarql.kdl.ir.StaticField;
import com.xarql.kdl.names.CommonText;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

public class CompilationDispatcher implements CommonText {

	public static final File DEFAULT_INPUT = new File(System.getProperty("user.dir")); // default to current working directory
	public static final File DEFAULT_OUTPUT = new File(System.getProperty("user.dir"), "/target/classes/");
	public static final int THREADS = Runtime.getRuntime().availableProcessors();


	public static final FileFilter KDL_FILTER = new RegexFileFilter(".*\\.kdl"); // default to all .kdl files

	public static final String QUIET = "quiet";
	public static final boolean DEFAULT_QUIET = false;

	private final File input;
	private final FileFilter filter;
	private final File output;

	public final TrackedMap<Constant, kdl.ConstantDefContext> constants = new TrackedMap<>();
	public final TrackedMap<StaticField, kdl.FieldDefContext> fields = new TrackedMap<>();
	public final Set<MethodHeader> methods = Collections.newSetFromMap(new ConcurrentHashMap<>());

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
			this.output = DEFAULT_OUTPUT;
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
			final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
			for(CompilationUnit unit : units) {
				threadPool.execute(unit);
			}
			threadPool.shutdown();
			try {
				threadPool.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				// continue
			}
		}
		for(CompilationUnit unit : units)
			unit.write();
		return this;
	}

	public void printDirs() {
		System.out.println("Input Directory: " + input);
		System.out.println("Output Directory: " + output);
	}

	private void compile(final BestList<CompilationUnit> units) {
		System.out.println("Compiling " + units.size() + " units...");
		final ElapseTimer et = new ElapseTimer();
		for(int pass = 0; pass < CompilationUnit.PASSES; pass++) {
			final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
			for(CompilationUnit unit : units) {
				try {
					threadPool.execute(unit);
				} catch(Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
			threadPool.shutdown();
			try {
				threadPool.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				// continue
			}
		}
		for(CompilationUnit unit : units) {
			try {
				unit.write();
			} catch(IOException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		System.out.println("Finished compiling in " + et);
	}

	public BestList<CompilationUnit> registerCompilationUnits() {
		return registerCompilationUnits(input, new BestList<>());
	}

	public BestList<CompilationUnit> registerCompilationUnits(final File f, final BestList<CompilationUnit> units) {
		if(f.isDirectory()) {
			for(File sub : Objects.requireNonNull(f.listFiles())) {
				registerCompilationUnits(sub, units);
			}
		} else if(filter.accept(f)) {
			units.add(new CompilationUnit(this, f, output));
		}
		return units;
	}

}
