package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class AndTest {

    @Test
    public void testAnd() {
        new StandardKdlTest("/assert/appenders", "AndTest").testKDL();
    }
}
