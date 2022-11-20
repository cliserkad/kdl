package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class ExpressionInComparisonTest {
	@Test
	public void testExpressionInComparison() {
		new StandardKdlTest("ExpressionInComparison", null, list("passpass")).testKDL();
	}
}
