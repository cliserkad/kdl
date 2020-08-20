package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class NicePrimitivesTest {
    public static final String OUTPUT = "true" + "120" + "32000" + "C" + "64000" + "1.5" + "string";

    @Test
    public void testTheOnesThatAreNormal() {
        new StandardKdlTest("/basics", "AllPrimitives", null, list(OUTPUT)).testKDL();
    }

    @Test
    public void testTheOnesThatPissMeOff() {
        // do nothing; unimplemented
    }
}
