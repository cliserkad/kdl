package com.xarql.kdl;

import java.util.ArrayList;
import java.util.List;

public class Util {

	public static void main(String[] args) {
		for (int a = 0; a < 5; a++)
			for (int b = 0; b < 5; b++)
				System.out.println("pow(" + a + ", " + b + ") == " + pow(a, b));
	}

	public static long pow(int num, int exp) {
		if (exp == 0)
			return 1;
		else if (exp == 1 || num == 0 || num == 1)
			return num;
		else {
			long out = num;
			while (exp-- > 1) {
				out *= num;
			}
			return out;
		}
	}

	public static int max(int a, int b) {
		if (a > b)
			return a;
		else
			return b;
	}

	public static int min(int a, int b) {
		if (a < b)
			return a;
		else
			return b;
	}

	public static <E> List<E> nonNullList(List<E> in) {
		if (in == null)
			return new ArrayList<>();
		else
			return in;
	}

}
