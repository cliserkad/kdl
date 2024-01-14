package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class ElseIfTest {

	@Test
	public void testElseIf() {
		new StandardKdlTest("ElseIf", null, list("pass")).testKDL();
	}

	@Test
	public void testIfElse() {
		new StandardKdlTest("IfElse", null, list("passpass")).testKDL();
	}

	@Test
	public void testElseIfNoBrackets() {
		new StandardKdlTest("ElseIfNoBrackets", null, list("pass")).testKDL();
	}

	@Test
	public void testIfElseNoBrackets() {
		new StandardKdlTest("IfElseNoBrackets", null, list("passpass")).testKDL();
	}

}
