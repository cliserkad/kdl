package com.xarql.kdl.plugin;

import com.xarql.kdl.CompilationDispatcher;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.xarql.kdl.*;
import test.java.ProcessOutput;

import java.io.File;
import java.nio.file.Path;

/**
 * Goal which touches a timestamp file.
 *
 * @goal kdl
 * 
 * @phase compile
 */
public class KDLCompilerPlugin extends AbstractMojo {

    /**
     * Location of the file.
     * @parameter property="project.build.directory"
     * @required
     */
    private File outputDirectory;

    /**
     * Location of .kdl files
     * @parameter property="project.build.sourceDirectory"
     * @required
     */
    private File sourceDirectory;

    public void execute() throws MojoExecutionException {
        CompilationDispatcher dispatcher = new CompilationDispatcher(sourceDirectory, CompilationDispatcher.KDL_FILTER, new File(outputDirectory + "/classes"));
        dispatcher.dispatch();
    }

}
