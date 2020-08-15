# kdl
cliserkad's Language. An alternative language for the JVM based on simplicity and usability

This repository holds the code required to compile `.kdl` source files to `.class` files. The compiled files can be run in the JVM and used in Java without any modification to the runtime environment.

### Use
The easiest way to get started is to use [kdl-base](https://github.com/cliserkad/kdl-base) as a template to make a new repository.
Since downloading the plugin from GitHub packages requires you to make a `settings.xml` file for Maven, it may be easier to [install from source](#installing-from-source).
Alternatively, you can manually configure a `pom.xml` to use the [kdl-maven-plugin](https://github.com/cliserkad/kdl/packages/358997).

### Installing From Source
Clone this repo and install with Maven.
```shell script
git clone https://github.com/cliserkad/kdl
cd kdl
mvn clean install
```

### Contributing
To contribute, you must [install from source](#installing-from-source)
Afterwards, you will be ready to mess around with the source code. Once you are finished making commits, fork this repo, 
push your commits to your fork, and open a pull request.


### Command Line
After you have completed the Contributing version of installation, you can compile .kdl files using the CompilationDispatcher. It will automatically use the current working directory as its root and recursively find files. You may use a 
regex as the first argument for the CompilationDispatcher in order to filter filenames. A simple use would be to limit to a single file, with an argument like `HelloWorld.kdl`.
```shell script
cd java-compiler/src/main/java/com/xarql/kdl
java CompilationDispatcher HelloWorld.kdl
cd test
java HelloWorld
```

### License
This repository uses the [MIT License](https://github.com/cliserkad/kdl/blob/master/LICENSE.txt). It is extremely permissive, and only requires that you include the copyright notice and itself with derived works. `class` files generated 
using this repository's code and the `kdl` files that you write do not require any license nor copyright notice.

### Contact
If you'd like to reach me through Discord, you can [join the support server](https://discord.gg/NR6E9Jt).
