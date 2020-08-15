package com.xarql.kdl.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

        getLog().info("Compiling .kdl Source Files");
    }

}
