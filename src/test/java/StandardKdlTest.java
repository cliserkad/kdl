package test.java;

import com.xarql.kdl.BestList;
import com.xarql.kdl.CompilationDispatcher;
import org.apache.commons.io.filefilter.RegexFileFilter;

import static com.xarql.kdl.BestList.list;

public class StandardKdlTest {
    public static final String JAVA_CMD = "java -cp src/test/kdl";

    public  final String           className;
    public  final String           pathExtension;
    private final BestList<String> arguments;
    private final BestList<String> expectedOutputs;

    public StandardKdlTest(final String pathExtension, final String className, final BestList<String> arguments, final BestList<String> expectedOutputs) {
        if(pathExtension == null)
            this.pathExtension = "";
        else
            this.pathExtension = pathExtension;

        this.className = className;

        if(arguments != null && !arguments.isEmpty())
            this.arguments = arguments;
        else
            this.arguments = list("");

        if(expectedOutputs != null && !expectedOutputs.isEmpty())
            this.expectedOutputs = expectedOutputs;
        else
            throw new IllegalArgumentException("You must provide at least one expected output for " + getClass().getName());

        if(this.arguments.size() != this.expectedOutputs.size())
            throw new IllegalStateException("arguments and expectedOutputs must have the same length. Check both inputs on " + getClass().getName() + " constructor");
    }

    public void testKDL() {
        try {
            // compile .kdl file
            new CompilationDispatcher(new RegexFileFilter(fileName())).dispatchSilently();
            // run .class file
            for(int i = 0; i < arguments.size(); i++) {
                ProcessOutput process = ProcessOutput.runProcess(JAVA_CMD + pathExtension + " " + className);
                assert process.getOutput().spread().equals(expectedOutputs.get(i)) || process.getOutput().spread().equals(expectedOutputs.get(i) + "\n");
                assert process.getErrors().isEmpty();
            }
        } catch(Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    public String fileName() {
        return className + ".kdl";
    }
}
