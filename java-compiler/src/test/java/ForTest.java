package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class ForTest {

	@Test
	public void testForRange() {
		new StandardKdlTest("ForRange", null, list("01234567890123456789123456789")).testKDL();
	}

	@Test
	public void testForArgs() {
		new StandardKdlTest("ForArgs", list("hello world"), list("helloworld")).testKDL();
	}

}
