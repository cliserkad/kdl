package com.xarql.kdl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Lexer {
    public static final File testSource = new File(System.getProperty("user.home") + "/Documents/kdl/hello_world.kdl");

    public static void main(String[] args) throws IOException {
        System.out.println(getTokens(testSource).spread());
    }

    public static BestList<String> getTokens(File source) throws IOException {
        String code = new String(Files.readAllBytes((testSource.toPath())));
        BestList<String> tokens = new BestList<>();
        boolean inComment = false;
        boolean inString = false;
        String partial = "";
        for(int i = 0; i < code.length(); i++) {
            final char c = code.charAt(i);
            if(c == '/' && code.charAt(i + 1) == '/')
                inComment = true;
            else if(!inComment) {
                if(isSyntax(c)) {
                    partial = append(tokens, partial);
                    tokens.add("" + c);
                    if(c == '"') {
                        partial = append(tokens, partial);
                        inString = !inString;
                    }
                }
                else if(Character.isWhitespace(c) && !inString) {
                    partial = append(tokens, partial);
                }
                else
                    partial += c;
            }
            else if(c == '\n')
                inComment = false;
        }
        return tokens;
    }

    public static String append(BestList<String> tokens, String partial) {
        if(!partial.trim().equals("")) {
            tokens.add(partial);
            return "";
        }
        else
            return partial;
    }

    public static final BestList<Character> SYNTAX = new BestList<>('{', '}', '(', ')', ',', ';', '"', '/', '*', '+', '-', '?', '=', '!');

    public static boolean isSyntax(char c) {
        return SYNTAX.contains(c);
    }
}
