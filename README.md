# kdl
cliserkad's Language. An alternative language for the JVM based on simplicity and usability

This repository holds the code required to compile `.kdl` source files to `.class` files. The compiled files can be run in the JVM and used in Java without any modification to the runtime environment.

The language is still very much in its alpha stage. Please look in [com.xarql.kdl.test](https://github.com/cliserkad/kdl/tree/master/src/com/xarql/kdl/sample) to see what has been implemented.

### Use
After cloning in to the repository, use Maven to download dependenices, build the Antlr 4 grammar, and compile the Java files. 
```console
git clone https://github.com/cliserkad/kdl
cd kdl
mvn antlr4:antlr4 clean install
cd src/com/xarql/kdl
java ClassCreator
cd test
java HelloWorld
```

### Changing Source Directory
Edit the `DEFAULT_LOC` variable in `ClassCreator` to specify the directory that your `.kdl` files are in.
