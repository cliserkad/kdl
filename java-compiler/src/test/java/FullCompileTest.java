package test.java;

import com.xarql.kdl.CompilationDispatcher;
import org.junit.jupiter.api.Test;

public class FullCompileTest {

	@Test
	public void testFull() {
		try {
			new CompilationDispatcher().dispatchQuietly();
			assert true;
		} catch(Exception e) {
			assert false;
		}
	}

}
