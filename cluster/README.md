# About this application

Provides different sets of operations to be performed in a clustered
environment.  Available apps:

  * loop that increments a counter
  * loop that prints the value of the counter 


# Compiling

Just do:

    mvn package
    
The previous command compiles and creates the a JAR with all the code
application.  You may control the backend in use by editing the Maven property
`code.generator.class`.  This can be done either by editing the property in
the `pom.xml` file, or directly on the command line by adding the following
switch to maven commands:

     -Dcode.generator.class=pt.ist.fenixframework.backend.jvstm.JVSTMCodeGenerator

You can replace the code generator class name with another,
e.g. `pt.ist.fenixframework.backend.jvstm.infinispan.JvstmIspnCodeGenerator`.

# Running

The following example, launches two 'incrementers' and one 'printer'.

    mvn exec:java\
    -Dexec.mainClass="pt.ist.fenixframework.example.cluster.Increment"

    mvn exec:java\
    -Dexec.mainClass="pt.ist.fenixframework.example.cluster.Increment"

    mvn exec:java\
    -Dexec.mainClass="pt.ist.fenixframework.example.cluster.Print"

    
