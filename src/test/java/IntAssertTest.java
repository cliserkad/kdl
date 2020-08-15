package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class IntAssertTest {

    @Test
    public void testIntAssert() {
        new StandardKdlTest("/assert", "IntAssert").testKDL();
    }
}
