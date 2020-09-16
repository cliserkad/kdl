package test.java;

import com.xarql.kdl.BestList;
import com.xarql.kdl.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.jupiter.api.Test;

public class FizzBuzzTest {

	public static final int[] NUMS = { -50, -30, -15, -5, -3, -1, 0, 1, 3, 5, 15, 30, 40, 45, 50, 99, 22 };

	public static void main(String[] args) {
		new FizzBuzzTest().testFizzBuzz();
	}

	@Test
	public void testFizzBuzz() {
		BestList<String> fizzBuzzArguments = new BestList<String>();
		for(int n : NUMS)
			fizzBuzzArguments.add("" + n);
		BestList<String> fizzBuzzOutputs = new BestList<String>();
		for(int n : NUMS)
			fizzBuzzOutputs.add(fizzBuzz(n));

		new StandardKdlTest("/sample", "FizzBuzz", fizzBuzzArguments, fizzBuzzOutputs);
	}

	public String fizzBuzz(int input) {
		String out = "";
		if(input % 3 == 0)
			out += "Fizz";
		if(input % 5 == 0)
			out += "Buzz";
		return out;
	}

}
