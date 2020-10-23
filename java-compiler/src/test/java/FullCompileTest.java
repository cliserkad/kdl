package test.java;

import com.xarql.kdl.CompilationDispatcher;
import org.junit.jupiter.api.Test;

public class FullCompileTest {

	@Test
	public void testFull() {
		try {
			new CompilationDispatcher(null, null, null).dispatchQuietly();
			assert true;
		} catch(Exception e) {
			e.printStackTrace();
			assert false;
		}
	}

}
