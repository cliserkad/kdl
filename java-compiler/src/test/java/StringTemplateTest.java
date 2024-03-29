package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class StringTemplateTest {

	@Test
	public void testTemplate() {
		new StandardKdlTest("StringTemplate", null, list("STR is val1 and a is val2")).testKDL();
	}

	@Test
	public void testEscape() {
		new StandardKdlTest("StringTemplate2", null, list("$10 is less than $55 because 10 < 55$$$ money money money $$$")).testKDL();
	}

	@Test
	public void testStore() {
		new StandardKdlTest("StringTemplate3", null, list("Template with val1 and val2 stored to a string")).testKDL();
	}

	@Test
	public void testVarsEndingInNum() {
		new StandardKdlTest("StringTemplate4", null, list("99 is lower than 230")).testKDL();
	}

}
