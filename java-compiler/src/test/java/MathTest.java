package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class MathTest {

	@Test
	public void testIntComparisons() {
		new StandardKdlTest("IntComparisons", null, list("pass   0 == 1pass   0 != 1pass   0 < 1pass   0 <= 1pass   0 > 1pass   0 >= 1done")).testKDL();
	}

	@Test
	public void testFloatComparisons() {
		new StandardKdlTest("FloatComparisons", null, list("pass   0.0 == 1.0pass   0.0 != 1.0pass   0.0 < 1.0pass   0.0 <= 1.0pass   0.0 > 1.0pass   0.0 >= 1.0done")).testKDL();
	}

	@Test
	public void testLongComparisons() {
		new StandardKdlTest("LongComparisons", null, list("pass   0 == 1pass   0 != 1pass   0 < 1pass   0 <= 1pass   0 > 1pass   0 >= 1done")).testKDL();
	}

	@Test
	public void testDoubleComparisons() {
		new StandardKdlTest("DoubleComparisons", null, list("pass   0.0 == 1.0pass   0.0 != 1.0pass   0.0 < 1.0pass   0.0 <= 1.0pass   0.0 > 1.0pass   0.0 >= 1.0done")).testKDL();
	}

	@Test
	public void testAllMathOperations() {
		new StandardKdlTest("AllMathOperations", null, list("All math operations passed")).testKDL();
	}

}
