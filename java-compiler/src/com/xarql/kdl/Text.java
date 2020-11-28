package com.xarql.kdl;

public class Text {

	public static final char[] PUNCTUATION_MARKS = { ':', '.', ',', ';', '?', '!', '"', '\'' };
	public static final char[] ESCAPE_CHARS = { '\n', '\t', '\r', '\f' }; // newline, tab, return, feed

	public static boolean a_z(char c) {
		int val = c;
		return val > 96 && val < 123;
	}

	public static boolean A_Z(char c) {
		int val = c;
		return val > 64 && val < 91;
	}

	/**
	 * Tests to see if a character is part of ASCII or not
	 *
	 * @param c any char
	 * @return whether ASCII contains c
	 */
	public static boolean isAscii(char c) {
		int val = c;
		if(val > 31 && val < 127) // standard ascii characters
			return true;

		// if c is an escape character such as newline or tab
		for(int i = 0; i < ESCAPE_CHARS.length; i++)
			if(c == ESCAPE_CHARS[i])
				return true;

		return false; // default
	}

	public static boolean isAscii(String text) {
		for(int i = 0; i < text.length(); i++)
			if(!isAscii(text.charAt(i)))
				return false;
		return true;
	}

	/**
	 * Only allow a-z and A-Z
	 *
	 * @param word
	 * @return true if all chars are in the latin alphabet
	 */
	public static boolean isLatinWord(String word) {
		for(int i = 0; i < word.length(); i++)
			if(!a_z(word.charAt(i)) && !A_Z(word.charAt(i)))
				return false;
		return true;
	}

	/**
	 * only allow a-z, A-Z and whitespace
	 *
	 * @param in A supposedly latin word
	 * @return true if all chars are latin
	 */
	public static boolean isLatin(String in) {
		for(int i = 0; i < in.length(); i++)
			if(!a_z(in.charAt(i)) && !A_Z(in.charAt(i)) && !Character.isWhitespace(in.charAt(i)))
				return false;
		return true;
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static String checkNotEmpty(String s) {
		if(isEmpty(s))
			throw new IllegalArgumentException("Provided string may not be null nor empty");
		return s;
	}

	public static String nonNull(String s) {
		if(s == null)
			return "";
		else
			return s;
	}

	public static boolean isPunctuation(char c) {
		for(char mark : PUNCTUATION_MARKS)
			if(c == mark)
				return true;
		return false;
	}

	public static String removePunctuation(String s) {
		String output = "";
		for(int i = 0; i < s.length(); i++) {
			if(!isPunctuation(s.charAt(i)))
				output += s.charAt(i);
		}
		return output;
	}

	public static String larger(String preferred, String other) {
		if(other == null)
			return preferred;
		else if(other.length() > preferred.length())
			return other;
		else
			return preferred;
	}

	public static String largest(String... input) {
		// null protection
		if(input == null || input.length == 0)
			return "";
		else {
			String output = input[0];
			for(String s : input)
				output = larger(output, s);
			return output;
		}
	}

	public static String largest(Iterable<String> input) {
		// null protection
		if(input == null)
			return "";
		else {
			String output = null;
			for(String s : input)
				output = larger(s, output);
			return output;
		}
	}

	public static String smaller(String preferred, String other) {
		if(other == null)
			return preferred;
		else if(other.length() < preferred.length())
			return other;
		else
			return preferred;
	}

	public static String smallest(String... input) {
		// null protection
		if(input == null || input.length == 0)
			return "";
		else {
			String output = input[0];
			for(String s : input)
				output = smaller(output, s);
			return output;
		}
	}

	public static String smallest(Iterable<String> input) {
		// null protection
		if(input == null)
			return "";
		else {
			String output = null;
			for(String s : input)
				output = larger(output, s);
			return output;
		}
	}

	/**
	 * Turns CamelCaseNames in to lower_case_names
	 *
	 * @param input CamelCase String
	 * @return lower_case_string
	 */
	public static String undoCamelCase(String input) {
		String output = "";
		for(int i = 0; i < input.length(); i++) {
			if(!Character.isAlphabetic(input.charAt(i)) && input.charAt(i) != ';')
				output += "-";
			if(i == 0)
				output += (input.charAt(0) + "").toLowerCase();
			else {
				if(Character.isUpperCase(input.charAt(i)))
					output += "_" + (input.charAt(i) + "").toLowerCase();
				else
					output += "" + input.charAt(i);
			}
		}
		return output;
	}

	public static boolean isFirstLetterUppercase(String in) {
		return !isEmpty(in) && Character.isUpperCase(in.charAt(0));
	}

	public static boolean isFirstLetterLowercase(String in) {
		return !isEmpty(in) && Character.isLowerCase(in.charAt(0));
	}

	public static boolean hasUppercase(String in) {
		if(isEmpty(in))
			return false;
		else {
			for(int i = 0; i < in.length(); i++)
				if(Character.isUpperCase(in.charAt(i)))
					return false;
			return true;
		}
	}

	public static boolean hasLowercase(String in) {
		if(isEmpty(in))
			return false;
		else {
			for(int i = 0; i < in.length(); i++)
				if(Character.isLowerCase(in.charAt(i)))
					return false;
				return true;
		}
	}

}
