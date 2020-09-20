package test.java;

import com.xarql.kdl.BestList;
import com.xarql.kdl.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;

import static com.xarql.kdl.BestList.list;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StandardKdlTest {

	public static final String JAVA_CMD = "java -cp src/";

	public final String className;
	public final String pathExtension;
	private final BestList<String> arguments;
	private final BestList<String> expectedOutputs;

	/**
	 * Makes a StandardKdlTest
	 * 
	 * @param pathExtension   extension on /src/test/kdl for java classpath
	 * @param className       name of class
	 * @param arguments       a list of sets of command line arguments
	 * @param expectedOutputs a list of expected outputs for each set of arguments
	 */
	public StandardKdlTest(final String pathExtension, final String className, final BestList<String> arguments, final BestList<String> expectedOutputs) {
		if(pathExtension == null)
			this.pathExtension = "";
		else
			this.pathExtension = pathExtension;

		this.className = className;

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
	 * @param pathExtension extension on /src/test/kdl for java classpath
	 * @param className     name of class
	 */
	public StandardKdlTest(final String pathExtension, final String className) {
		this(pathExtension, className, null, null);
	}

	public void testKDL() {
		try {
			// compile .kdl file
			new CompilationDispatcher(new RegexFileFilter(fileName())).dispatchQuietly();
			// run .class file
			for(int i = 0; i < arguments.size(); i++) {
				ProcessOutput process = ProcessOutput.runProcess(JAVA_CMD + pathExtension + " " + className + " " + arguments.get(i));
				assertEquals(expectedOutputs.get(i), process.getOutput().squish());
				// disregard exit code and errors
				// assertTrue(process.getErrors().isEmpty());
				// assertEquals(0, process.getExitValue());
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
		if(className.contains("."))
			return ".*" + className.substring(className.lastIndexOf('.') + 1) + ".kdl";
		else
			return className + ".kdl";
	}

}
