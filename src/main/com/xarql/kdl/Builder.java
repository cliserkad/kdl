package main.com.xarql.kdl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Builder {
    public static final File loc = new File(System.getProperty("user.home") + "/Documents/kdl/Test.kdl");

    public static void main(String[] args) throws IOException {
        String fileContent = new String(Files.readAllBytes(loc.toPath()));
        kdlLexer lex = new kdlLexer(CharStreams.fromString(fileContent));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        kdlParser parser = new kdlParser(tokens);
        ParseTree tree = parser.source();
        ParseTreeWalker.DEFAULT.walk(new SourceListener(), tree);
    }
}
