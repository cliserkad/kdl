package main.com.xarql.kdl;

public class NameFormats {
    public static final String INIT   = "<init>";
    public static final String CLINIT = "<clinit>";

    public static final char[] BANNED_IN_UNQUALIFIED_NAME = { '.', ';', '[', '/', ':' };

    public static boolean isUnqualifiedName(String str) {
        /* An unqualified name must not contain any of the ASCII characters . ; [ /
         * (that is, period or semicolon or left square bracket or forward slash). */
        for(char c : BANNED_IN_UNQUALIFIED_NAME)
            if(str.contains("" + c))
                return false;

        /* Method names are further constrained so that, with the exception of the
         * special method names <init> and <clinit> (�2.9), they must not contain the
         * ASCII characters < or > (that is, left angle bracket or right angle
         * bracket). */
        if(!str.equals(INIT) && !str.equals(CLINIT))
            if(str.contains("<") || str.contains(">"))
                return false;

        return true; // default
    }

    /** Provides the internal name of the given class with L prepended and ;
     * appended
     * 
     * @param c Class of the object
     * @return L + internal name + ; */
    public static String internalObjectName(Class<?> c) {
        return "L" + internalName(c) + ";";
    }

    public static String internalName(Class<?> c) {
        String out = c.getName().replace('.', '/');
        assert isInternalName(out);
        return out;
    }

    public static boolean isInternalName(String str) {
        /* In this internal form, the ASCII periods (.) that normally separate the
         * identifiers which make up the binary name are replaced by ASCII forward
         * slashes (/). The identifiers themselves must be unqualified names
         * (�4.2.2). */
        if(str.contains("."))
            return false;
        String[] parts = str.split("/");
        for(String p : parts)
            if(!isUnqualifiedName(p))
                return false;

        return true;
    }

    public static boolean isKDLVariableName(String in) {
        return !Text.isLatinWord(in);
    }

    public static void checkKDLVariableName(String in) {
        if(!isKDLVariableName(in))
            throw new IllegalArgumentException("The string " + in + " is not a valid kdl variable name. Variable names are limited to the characters a-z and A-Z only");
    }

    /** Determines if the input String has characters that are only either in the
     * range A-Z or are _underscores
     * 
     * @param in name of const
     * @return if name is suitable for a const */
    public static boolean isKDLConstName(String in) {
        for(int i = 0; i < in.length(); i++)
            if(!Text.A_Z(in.charAt(i)) && in.charAt(i) != '_')
                return false;
        return true;
    }

    public static String checkKDLConstName(String in) {
        if(!isKDLConstName(in))
            throw new IllegalArgumentException("The string " + in + " is not a valid kdl const name. Const names are limited to the characters A-Z and _ only");
        return in;
    }

    public static boolean isKDLTypeName(String in) {
        return Text.isLatinWord(in) && Text.firstLetterIsUppercase(in);
    }

    public static String checkKDLTypeName(String in) {
        if(!isKDLTypeName(in))
            throw new IllegalArgumentException("The string " + in + " is not a valid kdl type name. Type names are limited to the latin alphabet and must start with an uppercase letter");
        return in;
    }
}
