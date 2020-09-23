package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class FieldTest {

	@Test
	public void testFieldInstantiation() {
		new StandardKdlTest(null, "test.kdl.obj.Car", null, list("success")).testKDL();
	}

	@Test
	public void testFieldAccess() {
		new StandardKdlTest(null, "test.kdl.obj.Car2", null, list("1998 Honda Accord")).testKDL();
	}

}
