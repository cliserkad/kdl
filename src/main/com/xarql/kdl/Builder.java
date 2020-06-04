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

    private File source;
    private kdlParser parser;
    private SourceListener sl;
    protected Source src;

    public static void main(String[] args) throws IOException {
        Builder b = new Builder(loc);
        b.build();
        System.out.println(b.src.constants);
    }

    public Builder(File source) {
        this.source = source;
    }

    public void build() throws IOException {
        String fileContent = new String(Files.readAllBytes(source.toPath()));
        kdlLexer lex = new kdlLexer(CharStreams.fromString(fileContent));
        CommonTokenStream tokens = new CommonTokenStream(lex);
        parser = new kdlParser(tokens);
        ParseTree tree = getParser().source();
        sl = new SourceListener(this);
        ParseTreeWalker.DEFAULT.walk(sl, tree);
    }

    public kdlParser getParser() {
        return parser;
    }
}
