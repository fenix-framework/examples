# About this application

This is a very simple hello-world-like application that shows the basics of
how to develop an application using the Fenix Framework.  Have a look at the
Domain Model file in
[src/main/dml/helloworld.dml](src/main/dml/helloworld.dml).

In this application we use the file-based configuration.  The
`fenix-framework-<backend-here>.properties` files allows for automatic
initialization of the framework (replace `<backend-here>` with the name of the
backend to configure).

# Compiling

Just do:

    mvn package
    
The previous command compiles and creates the hello-world JAR application.
You may control the backend in use by editing the Maven property
`code.generator.class`.  This can be done either by editing the
property in the `pom.xml` file, or directly on the command line by adding the
following switch to maven commands:

     -Dcode.generator.class=pt.ist.fenixframework.backend.ogm.OgmCodeGenerator

You can replace the code generator class name with another,
e.g. `pt.ist.fenixframework.backend.infinispan.InfinispanCodeGenerator`.

# Running

The following runs the program passing three names as arguments:

    mvn exec:java \
     -Dexec.mainClass="pt.ist.fenixframework.example.helloworld.Main" \
     -Dexec.args="john doe smith"
    
    
    
