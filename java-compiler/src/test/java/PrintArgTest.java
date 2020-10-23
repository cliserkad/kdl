package test.java;

import com.xarql.kdl.BestList;
import org.junit.jupiter.api.Test;

public class PrintArgTest {

	public static final String[] ARGS = { "hello", "test" };

	public static void main(String[] args) {
		new PrintArgTest().testPrintArg();
	}

	@Test
	public void testPrintArg() {
		BestList<String> printArgArguments = new BestList<>(ARGS);
		new StandardKdlTest("PrintArg", printArgArguments, printArgArguments);
	}

}
