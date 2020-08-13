package test.java;

import com.xarql.kdl.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.jupiter.api.Test;

import static test.java.HelloWorldTest.JAVA_CMD;

public class FizzBuzzTest {
    public static final String FIZZBUZZ_CMD = JAVA_CMD + "/sample FizzBuzz ";
    public static final int[] NUMS = { -50, -30, -15, -5, -3, -1, 0, 1, 3, 5, 15, 30, 40, 45, 50, 99, 22 };

    public static void main(String[] args) {
        new FizzBuzzTest().testFizzBuzz();
    }

    @Test
    public void testFizzBuzz() {
        try {
            // compile FizzBuzz.kdl
            new CompilationDispatcher(new RegexFileFilter("FizzBuzz.kdl"), true).compileAll();

            for(int n : NUMS)
                fizzBuzzCase(n);

        } catch(Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    private void fizzBuzzCase(int input) throws Exception {
        ProcessOutput process = ProcessOutput.runProcess(FIZZBUZZ_CMD + input);
        assert process.getOutput().spread().equals(fizzBuzz(input) + "\n") || process.getOutput().spread().equals(fizzBuzz(input));
    }

    public String fizzBuzz(int input) {
        String out = "";
        if(input % 3 == 0)
            out += "Fizz";
        if(input % 5 == 0)
            out += "Buzz";
        return out;
    }
}
