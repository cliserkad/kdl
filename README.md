# kdl
cliserkad's Language. An alternative language for the JVM based on simplicity and usability

This repository holds the code required to compile `.kdl` source files to `.class` files. The compiled files can be run in the JVM and used in
Java without any modification to the runtime environment.

The language is still very much in its alpha stage. Please look in [com.xarql.kdl.sample](https://github.com/cliserkad/kdl/tree/master/src/com/xarql/kdl/sample)
to see what has been implemented.

### Use
Download the dependencies specified in `pom.xml` using Maven. Compile the `kdl.g4` grammar; enter the command `mvn antlr4:antlr4` to use Antlr 
to generate neccessary Java sources. `SourceListener.java` is the main interpreter for the language and is dependent on the parser files created
by Antlr. Link the generated sources to the build path for `SourceListener` in your IDE and run the file `ClassCreator.java`. 

### Changing Source Directory
Edit the `DEFAULT_LOC` variable in `ClassCreator` to specify the directory in which your `.kdl` files are present. This location is pre-set to 
`C:/Users`/_your username_`/Documents/kdl` on Windows and `usr/`_your username_`/home/documents/kdl` on Linux. For testing, you can copy the files found 
in `com.xarql.kdl.sample`
