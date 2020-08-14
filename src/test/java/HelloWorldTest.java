package test.java;

import com.xarql.kdl.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class HelloWorldTest {

	@Test
	public void testHelloWorld() {
		new StandardKdlTest(null, "HelloWorld", null, list("hello world")).testKDL();
	}

}
