package test.java;

import com.xarql.kdl.BestList;
import com.xarql.kdl.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;

import static com.xarql.kdl.BestList.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandardKdlTest {

	public static final String JAVA_CMD = "java -cp target/classes ";

	public final String clazz;
	private final BestList<String> arguments;
	private final BestList<String> expectedOutputs;

	/**
	 * Makes a StandardKdlTest
	 *
	 * @param clazz       name of class
	 * @param arguments       a list of sets of command line arguments
	 * @param expectedOutputs a list of expected outputs for each set of arguments
	 */
	public StandardKdlTest(final String clazz, final BestList<String> arguments, final BestList<String> expectedOutputs) {
		this.clazz = clazz;

		if(arguments != null)
			this.arguments = arguments;
		else
			this.arguments = list("");

		if(expectedOutputs != null)
			this.expectedOutputs = expectedOutputs;
		else
			this.expectedOutputs = list("");

		if(this.arguments.size() != this.expectedOutputs.size())
			throw new IllegalStateException("arguments and expectedOutputs must have the same length. Check both inputs on " + getClass().getName() + " constructor");
	}

	/**
	 * Makes a StandardKdlTest that has no arguments and no output. Use this
	 * constructor for .kdl files that use assert instead of printing.
	 *
	 * @param clazz     name of class
	 */
	public StandardKdlTest(final String clazz) {
		this(clazz, null, null);
	}

	public void testKDL() {
		try {
			// compile .kdl file
			new CompilationDispatcher(null, new RegexFileFilter(fileName()), null).dispatchQuietly();
			// run .class file
			for(int i = 0; i < arguments.size(); i++) {
				ProcessOutput process = ProcessOutput.runProcess(JAVA_CMD + clazz + " " + arguments.get(i));
				if(!process.getErrors().isEmpty()) {
					System.err.println("Encountered error running " + process.getCommand() + " @ " + System.getProperty("user.dir"));
					for(String s : process.getErrors())
						System.err.println(s);
				}
				assertTrue(process.getErrors().isEmpty());
				assertEquals(0, process.getExitValue());
				assertEquals(expectedOutputs.get(i), process.getOutput().squish());
			}
		} catch(Exception e) {
			e.printStackTrace();
			assert false;
		}
	}

	/**
	 * @return className + ".kdl"
	 */
	public String fileName() {
		if(clazz.contains("."))
			return ".*" + clazz.substring(clazz.lastIndexOf('.') + 1) + ".kdl";
		else
			return clazz + ".kdl";
	}

}
