# Compiling

Just do:

    mvn package
    
The previous command compiles and creates the hello-world JAR application.

# Running

To run it do, e.g.:

    mvn exec:java \
    -Dexec.mainClass="pt.ist.fenixframework.example.helloworld.Main" \
    -Dexec.args="john doe silver"
    
You may control the backend in use by editing the Maven property
`fenixframework.code.generator`.  This can be done either by editing the
property in the pom.xml file, or directly on the command line with:

    mvn package exec:java \
    -Dexec.mainClass="pt.ist.fenixframework.example.helloworld.Main" \
    -Dexec.args="john doe silver" \
    -Dfenixframework.code.generator=pt.ist.fenixframework.backend.ogm.OgmCodeGenerator
    
    
