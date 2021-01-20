package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class AmountLoopTest {

	@Test
	public void testAmountLoop() {
		new StandardKdlTest("AmountLoop", null, list(
				"This message should be printed 5 timesThis message should be printed 5 timesThis message should be printed 5 timesThis message should be printed 5 timesThis message should be printed 5 times"))
						.testKDL();
	}

}
