package test.java;

import com.xarql.kdl.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.jupiter.api.Test;

public class PrintArgTest {
    public static final String[] ARGS = { "hello", "test" };
    public static final String PRINTARG_CMD = StandardKdlTest.JAVA_CMD + "/basics PrintArg ";

    public static void main(String[] args) {
        new PrintArgTest().testPrintArg();
    }

    @Test
    public void testPrintArg() {
        try {
            // compile FizzBuzz.kdl
            new CompilationDispatcher(new RegexFileFilter("PrintArg.kdl"), true).compileAll();

            for(String arg : ARGS)
                printArgCase(arg);

        } catch(Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    private void printArgCase(String arg) throws Exception {
        ProcessOutput process = ProcessOutput.runProcess(PRINTARG_CMD + arg);
        assert process.getOutput().spread().equals(arg + "\n") || process.getOutput().spread().equals(arg);
    }

}
