package pt.ist.fenixframework.example.suspend;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.example.suspend.domain.App;
import pt.ist.fenixframework.example.suspend.domain.Person;

public class AddPerson {
    
    private static final Logger logger = LoggerFactory.getLogger(AddPerson.class);

    public static void main(final String[] args) {
        try {
            logger.info("Will add a person");
            addPerson();
        } finally {
            FenixFramework.shutdown();
        }
    }

    @Atomic
    private static void addPerson() {
        App app = FenixFramework.getDomainRoot().getApp();
        
        if (app == null) {
            logger.info("app is null");
            app = new App();
            logger.info("setting app {} on root {}", app.getExternalId(), FenixFramework.getDomainRoot().getExternalId());
            FenixFramework.getDomainRoot().setApp(app);
        }
        
        Set<Person> people = app.getPersonSet();

        for (Person p : people) {
            System.out.println(p.getName());
        }
        
        app.addPerson(new Person("John Doe " + people.size()));
    }
}
