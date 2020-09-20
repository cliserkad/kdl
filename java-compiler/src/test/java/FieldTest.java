package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class FieldTest {

	@Test
	public void testCar() {
		new StandardKdlTest(null, "test.kdl.obj.Car", null, list("success")).testKDL();
	}

}
