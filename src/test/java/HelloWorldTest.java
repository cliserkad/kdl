package test.java;

import com.xarql.kdl.CompilationDispatcher;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {

	public static void main(String[] args) {
		new HelloWorldTest().testHelloWorld();
	}

	@Test
	public void testHelloWorld() {
		try {
			// compile HelloWorld.kdl
			CompilationDispatcher.main(new String[] {"HelloWorld.kdl"});
			// run HelloWorld.class
			ProcessOutput helloWorld = ProcessOutput.runProcess("java -cp src/test/kdl HelloWorld");
			assert helloWorld.getOutput().spread().equals("hello world\n");
		} catch(Exception e) {
			e.printStackTrace();
			assert false;
		}
	}

}
