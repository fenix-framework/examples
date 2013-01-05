package pt.ist.fenixframework.example.helloworld;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.FenixFramework;

public class Main {
    public static void main(final String[] args) {
        try {
            addNewPeople(args);
            greetAll();
        } finally {
            FenixFramework.shutdown();
        }
    }

    @Atomic
    private static void addNewPeople(String[] args) {
        HelloWorldApplication app = new HelloWorldApplication();
        FenixFramework.getDomainRoot().setApp(app);
        for (String name : args) {
            app.addPerson(new Person(name, app));
        }
    }

    @Atomic
    private static void greetAll() {
        HelloWorldApplication app = FenixFramework.getDomainRoot().getApp();
        app.sayHello();
    }
}
