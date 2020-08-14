package test.java;

import com.xarql.kdl.CompilationDispatcher;
import com.xarql.kdl.CompilationUnit;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.jupiter.api.Test;

public class IncorrectFileNameTest {

    @Test
    public void testIncorrectFileName() {
        try {
            // compile .kdl file
            new CompilationDispatcher(new RegexFileFilter("incorrect file name.kdl")).dispatchSilently();
            assert false;
        } catch (Exception e) {
            assert e instanceof IllegalArgumentException && e.getMessage().contains(CompilationUnit.INCORRECT_FILE_NAME);
        }
    }

}
