package test.java;

import com.xarql.kdl.BestList;
import org.junit.jupiter.api.Test;

public class FibonacciTest {

    @Test
    public void testFibonacci() {
        BestList<String> arguments = new BestList<>();
        BestList<String> outputs = new BestList<>();
        for(int n = 0; n < 10; n++) {
            arguments.add("" + n);
            outputs.add("" + fib(n));
        }
        new StandardKdlTest("/sample", "Fibonacci", arguments, outputs);
    }

    static int fib(int n)
    {
        if (n <= 1)
            return n;
        return fib(n - 1) + fib(n - 2);
    }
}
