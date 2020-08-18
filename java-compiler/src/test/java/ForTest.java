package test.java;

import org.junit.jupiter.api.Test;

import static com.xarql.kdl.BestList.list;

public class ForTest {
    public static final String EXTENSION = "/flow/for";

    @Test
    public void testForRange() {
        new StandardKdlTest(EXTENSION, "ForRange", null, list("01234567890123456789123456789")).testKDL();
    }

    @Test
    public void testForArgs() {
        new StandardKdlTest(EXTENSION, "ForArgs", list("hello world"), list("helloworld")).testKDL();
    }
}
