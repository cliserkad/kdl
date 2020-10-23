package com.xarql.kdl.plugin;

import com.xarql.kdl.CompilationDispatcher;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * Goal which touches a timestamp file.
 *
 * @goal kdl
 * @phase compile
 */
public class KDLCompilerPlugin extends AbstractMojo {

	/**
	 * Location of the file.
	 *
	 * @parameter property="project.build.directory"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Location of .kdl files
	 *
	 * @parameter property="project.build.sourceDirectory"
	 * @required
	 */
	private File sourceDirectory;

	public void execute() throws MojoFailureException {
		// append a slash to the file path if it isn't a directory
		if(!outputDirectory.isDirectory())
			outputDirectory = new File(outputDirectory.getPath() + "/");
		outputDirectory = new File(outputDirectory, "classes/");
		getLog().info("Input Directory: " + sourceDirectory);
		getLog().info("Output Directory: " + outputDirectory);
		// dispatch compilation
		final CompilationDispatcher dispatcher = new CompilationDispatcher(sourceDirectory, CompilationDispatcher.KDL_FILTER, outputDirectory);
		try {
			dispatcher.dispatchQuietly();
		} catch(Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}

}
