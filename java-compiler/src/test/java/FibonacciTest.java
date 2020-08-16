package test.java;

import org.junit.jupiter.api.Test;

public class FibonacciTest {

    @Test
    public void testFibonacci() {
        int n = 10;
        System.out.println(fib(n));
    }

    static int fib(int n)
    {
        if (n <= 1)
            return n;
        return fib(n - 1) + fib(n - 2);
    }
}
