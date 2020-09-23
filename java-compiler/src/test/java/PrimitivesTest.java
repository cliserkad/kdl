package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class PrimitivesTest {

	public static final String OUTPUT0 = "true" + "120" + "32000" + "C" + "64000" + "1.5" + "string";
	public static final String OUTPUT1 = "133444444444444" + "9.2348936E7" + OUTPUT0;

	@Test
	public void testTheOnesThatAreNormal() {
		new StandardKdlTest("test/kdl/basics", "Primitives", null, list(OUTPUT0)).testKDL();
	}

	@Test
	public void testTheOnesThatPissMeOff() {
		new StandardKdlTest("test/kdl/basics", "AllPrimitives", null, list(OUTPUT1)).testKDL();
	}

}
