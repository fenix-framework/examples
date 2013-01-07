# Compiling

Just do:

    mvn package
    
The previous command compiles and creates the hello-world JAR application.
You may control the backend in use by editing the Maven property
`fenixframework.code.generator`.  This can be done either by editing the
property in the `pom.xml` file, or directly on the command line by adding the
following switch to maven commands:

     -Dfenixframework.code.generator=pt.ist.fenixframework.backend.ogm.OgmCodeGenerator

You can replace the code generator class name with another,
e.g. `pt.ist.fenixframework.backend.infinispan.InfinispanCodeGenerator`.

# Running

The following runs the program passing three names as arguments:

    mvn exec:java \
     -Dexec.mainClass="pt.ist.fenixframework.example.helloworld.Main" \
     -Dexec.args="john doe smith"
    
    
    
