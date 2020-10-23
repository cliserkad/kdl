package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class HelloWorldTest {

	@Test
	public void testHelloWorld() {
		new StandardKdlTest("HelloWorld", null, list("hello world")).testKDL();
	}

}
