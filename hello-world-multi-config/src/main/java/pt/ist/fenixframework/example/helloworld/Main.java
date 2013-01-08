package pt.ist.fenixframework.example.helloworld;

import java.net.URL;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.MultiConfig;
import pt.ist.fenixframework.backend.infinispan.InfinispanConfig;
import pt.ist.fenixframework.backend.ogm.OgmConfig;
import pt.ist.fenixframework.backend.mem.MemConfig;

public class Main {
    public static void main(final String[] args) {
        final URL [] dmlFiles =
            Config.resourcesToURLArray("fenix-framework-domain-root.dml",
                                       "fenix-framework-bplustree-domain-object.dml",
                                       "fenix-framework-adt-bplustree.dml",
                                       "fenix-framework-indexes.dml",
                                       "helloworld.dml");

        MultiConfig configs = new MultiConfig();
        configs.add(new MemConfig() {{
            domainModelURLs = dmlFiles;
        }});
        configs.add(new InfinispanConfig() {{
            domainModelURLs = dmlFiles;
            ispnConfigFile = "infinispanNoFile.xml";
        }});
        configs.add(new OgmConfig() {{
            domainModelURLs = dmlFiles;
            ispnConfigFile = "infinispanNoFile.xml";
        }});

        FenixFramework.initialize(configs);
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
