# kdl
cliserkad's Language. An alternative language for the JVM based on simplicity and usability

This repository holds the code required to compile `.kdl` source files to `.class` files. The compiled files can be run in the JVM and used in Java without any modification to the runtime environment.

The language is still very much in its alpha stage. Please look in [test](https://github.com/cliserkad/kdl/tree/master/src/test/kdl) to see what has been implemented.

### Use
Use Maven to automatically download the code from GitHub Packages. Add the [required dependency info](https://github.com/cliserkad/kdl/packages/353306) to your pom.xml and run `mvn install`. Invoke CompilationDispatcher.main() from your
Java code to compile .kdl files in the same directory.
```java
CompilationDispatcher.main(null);
```
Or you can instantiate CompilationDispatcher and invoke compileAll() to specify the input file and/or filter by filename.
```java
File file = new File("HelloWorld.kdl");
new CompilationDispatcher(file).compileAll();
```

### Contributing
Clone in to the repository, use Maven to download dependencies, build the Antlr 4 grammar, and compile the Java files. 
```shell script
git clone https://github.com/cliserkad/kdl
cd kdl
mvn clean install
```
Afterwards, you will be ready to mess around with the source code. Once you are finished making commits, fork this repo, 
push your commits to your fork, and open a pull request.


### Command Line
After you have completed the Contributing version of installation, you can compile .kdl files using the CompilationDispatcher. It will automatically use the current working directory as its root and recursively find files. You may use a 
regex as the first argument for the CompilationDispatcher in order to filter filenames. A simple use would be to limit to a single file, with an argument like `HelloWorld.kdl`.
```shell script
cd src/com/xarql/kdl
java CompilationDispatcher HelloWorld.kdl
cd test
java HelloWorld
```

### License
This repository uses the [MIT License](https://github.com/cliserkad/kdl/tree/master/LICENSE.txt). It is extremely permissive, and only requires that you include the copyright notice and itself with derived works. `class` files generated 
using this repository's code and the `kdl` files that you write do not require any license nor copyright notice.
