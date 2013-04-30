# About this application

This is a application that shows the basics of how to use transactions 
in an application using the Fenix Framework. 

The application provides a set of commands that allow the user to teste usage of
transactions.

Commands are run in one transaction untill a commit or a rollback of the
transaction occures. This is achieved by suspending the running transaction 
at the end of each command, which implies that at the start of a command 
we resume or start a transaction.

The application provides a REPL(Read-eval-print-loop) with the following commands:

    add     - adds a new person with a given name to the current transaction
    cancel  - rollbacks current the transaction
    done    - commits the transaction
    list    - lists all added people to the transaction
    quit    - exits the application
    rename  - changes a name of a previously added person
    show    - lists a person given its externalId


# Configuration

In this application we use the file-based configuration.  The
`fenix-framework-<backend-here>.properties` files allows for automatic
initialization of the framework (replace `<backend-here>` with the name of the
backend to configure).

# Compiling

Just do:

    mvn package
    
The previous command compiles and creates the suspend-resume JAR application.
You may control the backend in use by editing the Maven property
`code.generator.class`.  This can be done either by editing the
property in the `pom.xml` file, or directly on the command line by adding the
following switch to maven commands:

     -Dcode.generator.class=pt.ist.fenixframework.backend.ogm.OgmCodeGenerator

You can replace the code generator class name with another,
e.g. `pt.ist.fenixframework.backend.infinispan.InfinispanCodeGenerator`.

# Running

The following runs the program:

    mvn exec:java \
     -Dexec.mainClass="pt.ist.fenixframework.example.suspend.SuspendTest"
    
