package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class NeverLoopTest {

    @Test
    public void testNeverLoop() {
        new StandardKdlTest("/flow/while", "NeverLoop", null, list("hello from NeverLoop")).testKDL();
    }
}
