package com.xarql.kdl.plugin;

import com.xarql.kdl.CompilationDispatcher;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;

import java.io.File;

/**
 * Compiles KDL files in a Maven project
 */
@Mojo(
		name = "kdl",
		defaultPhase = LifecyclePhase.COMPILE,
		requiresDependencyResolution = ResolutionScope.TEST,
		requiresOnline = false,
		requiresProject = true
)
@Execute(
		goal = "kdl",
		phase = LifecyclePhase.COMPILE
)
public class KDLCompilerPlugin extends AbstractMojo {

	/**
	 * Location of compiled (output) .class files
	 */
	@Parameter(
			name ="outputDirectory",
			property ="project.build.directory",
			required = true,
			readonly = false
	)
	private File outputDirectory;

	/**
	 * Location of source (input) .kdl files
	 */
	@Parameter(
			name = "sourceDirectory",
			property = "project.build.sourceDirectory",
			required = true,
			readonly = false
	)
	private File sourceDirectory;

	/**
	 * Compile files
	 * @throws MojoFailureException Forwarded exceptions from compiler
	 */
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
