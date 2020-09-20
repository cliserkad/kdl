package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class StringIndexTest {

	@Test
	public void testStringIndex() {
		new StandardKdlTest("test/kdl/misc", "StringIndex", null, list("l")).testKDL();
	}

}
