package test.java;

import com.xarql.kdl.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {
	public static final String JAVA_CMD = "java -cp src/test/kdl";

	public static void main(String[] args) {
		new HelloWorldTest().testHelloWorld();
	}

	@Test
	public void testHelloWorld() {
		try {
			// compile HelloWorld.kdl
			new CompilationDispatcher(new RegexFileFilter("HelloWorld.kdl"), true).compileAll();
			// run HelloWorld.class
			ProcessOutput helloWorld = ProcessOutput.runProcess(JAVA_CMD + " HelloWorld");
			assert helloWorld.getOutput().spread().equals("hello world\n");
		} catch(Exception e) {
			e.printStackTrace();
			assert false;
		}
	}

}
