package test.java;

import com.xarql.kdl.BestList;
import org.junit.jupiter.api.Test;

public class IsEvenTest {

	public static final int[] NUMS = { -50, -30, -15, -5, -3, -1, 0, 1, 3, 5, 15, 30, 40, 45, 50, 99, 22 };

	@Test
	public void testIsEven() {
		BestList<String> arguments = new BestList<String>();
		for(int n : NUMS)
			arguments.add("" + n);
		BestList<String> outputs = new BestList<String>();
		for(int n : NUMS)
			outputs.add(isEven(n));

		new StandardKdlTest("test/kdl/sample", "IsEven", arguments, outputs);
	}

	public String isEven(int n) {
		if(n % 2 == 0)
			return "true";
		else
			return "false";
	}

}
