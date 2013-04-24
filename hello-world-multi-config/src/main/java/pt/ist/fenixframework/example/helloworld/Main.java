package pt.ist.fenixframework.example.helloworld;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.MultiConfig;
import pt.ist.fenixframework.backend.infinispan.InfinispanConfig;
import pt.ist.fenixframework.backend.jvstm.JVSTMConfig;
import pt.ist.fenixframework.backend.jvstm.infinispan.JvstmIspnConfig;
import pt.ist.fenixframework.backend.mem.MemConfig;
import pt.ist.fenixframework.backend.ogm.OgmConfig;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] args) {
        final URL [] dmlFiles =
            Config.resourcesToURLArray("fenix-framework-domain-root.dml",
                                       "fenix-framework-adt-bplustree.dml",
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
        configs.add(new JVSTMConfig() {{
            domainModelURLs = dmlFiles;
        }});
        configs.add(new JvstmIspnConfig() {{
            domainModelURLs = dmlFiles;
        }});

        FenixFramework.initialize(configs);
        try {
            logger.info("Will add new people");
            addNewPeople(args);
            logger.info("Will greet all");
            greetAll();
        } finally {
            FenixFramework.shutdown();
        }
    }

    @Atomic
    private static void addNewPeople(String[] args) {
        HelloWorldApplication app = FenixFramework.getDomainRoot().getApp();
        
        if (app == null) {
            logger.info("app is null");
            app = new HelloWorldApplication();
            logger.info("setting app {} on root {}", app.getExternalId(), FenixFramework.getDomainRoot().getExternalId());
            FenixFramework.getDomainRoot().setApp(app);
        }

        for (String name : args) {
            logger.info("addNewPeople: app is {}", app.getExternalId());
            app.addPerson(new Person(name));
        }
    }

    @Atomic
    private static void greetAll() {
        HelloWorldApplication app = FenixFramework.getDomainRoot().getApp();
        logger.info("greetAll: app is {}", app.getExternalId());
        app.sayHello();
    }
}
