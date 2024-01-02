package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class MathTest {

	@Test
	public void testIntComparisons() {
		new StandardKdlTest("IntComparisons", null, list("pass   0 == 1" + "pass   0 != 1" + "pass   0 < 1" + "pass   0 <= 1" + "pass   0 > 1" + "pass   0 >= 1" + "done")).testKDL();
	}

	@Test
	public void testAllMathOperations() {
		new StandardKdlTest("AllMathOperations", null, list("All math operations passed")).testKDL();
	}

}
