package com.xarql.kdl.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.xarql.kdl.*;
import test.java.ProcessOutput;

import java.io.File;

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
     * @parameter expression="project.build.directory"
     * @required
     */
    private File outputDirectory;

    public void execute() throws MojoExecutionException {
        CompilationDispatcher dispatcher = new CompilationDispatcher();
        dispatcher.dispatch();
    }

}
