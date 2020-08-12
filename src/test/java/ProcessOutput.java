package test.java;

import com.xarql.kdl.BestList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Holds the data from running a process via runProcess()
 */
public class ProcessOutput {
	private final String           command;
	private final BestList<String> output;
	private final BestList<String> errors;
	private final int              exitValue;

	private ProcessOutput(String command, BestList<String> output, BestList<String> errors, int exitValue) {
		this.output = output;
		this.errors = errors;
		this.exitValue = exitValue;
		this.command = command;
	}

	public ProcessOutput rerun() throws Exception {
		return runProcess(command);
	}

	public static ProcessOutput runProcess(String command) throws Exception {
		Process process = Runtime.getRuntime().exec(command);
		BestList<String> output = captureLines(process.getInputStream());
		BestList<String> errors = captureLines(process.getErrorStream());
		process.waitFor();
		return new ProcessOutput(command, output, errors, process.exitValue());
	}

	private static BestList<String> captureLines(InputStream ins) throws IOException {
		String line = null;
		BestList<String> output = new BestList<>();
		BufferedReader in = new BufferedReader(new InputStreamReader(ins));
		while((line = in.readLine()) != null) {
			output.add(line);
		}
		return output;
	}

	/**
	 * Returns the command used to invoke the process
	 * @return command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Copies the strings in output to a new list and returns that list
	 * @return copy of output
	 */
	public BestList<String> getOutput() {
		return new BestList<>(output);
	}

	/**
	 * Copies the strings in errors to a new list and returns that list
	 * @return copy of errors
	 */
	public BestList<String> getErrors() {
		return new BestList<>(errors);
	}

	/**
	 * Returns the exit value from the process
	 * @return exitValue
	 */
	public int getExitValue() {
		return exitValue;
	}
}
