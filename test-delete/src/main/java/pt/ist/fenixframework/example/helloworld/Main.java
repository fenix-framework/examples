package pt.ist.fenixframework.example.helloworld;

import jvstm.Atomic;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.Transaction;

public class Main {
    public static void main(final String[] args) {
	Config config = new Config() {
	    {
		domainModelPath = "/helloworld.dml";
		dbAlias = "//localhost:3306/test";
		dbUsername = "root";
		dbPassword = "";
		rootClass = HelloWorldApplication.class;
	    }
	};
	FenixFramework.initialize(config);
	testConnectToContactAfterDelete();
	// showContact("12884901890");
    }

    private static void testDelete() {
	Person a = addPerson("A");
	addPerson("B");
	addPerson("C");
	delete(a);
	showPerson(a);
    }

    private static void testConnectToContactAfterDelete() {
	Person personA = addPerson("A");
	addPerson("B");
	addPerson("C");
	delete(personA);
	reconnectPersonToRoot(personA);
	addContact(personA, "testContact");
	showPerson(personA);
    }

    @Atomic
    private static void showContact(String externalId) {
	Contact contact = AbstractDomainObject.fromExternalId(externalId);
	System.out.println(contact.getPhoneNumber());
	System.out.println(contact.getPerson().getName());
    }

    @Atomic(speculativeReadOnly = false)
    private static void addContact(Person person, String phoneNumber) {
	System.out.printf("Transaction number %d\n", Transaction.current().getNumber());
	System.out.printf("Add contact %s to %s\n", phoneNumber, person.getName());
	Contact contact = new Contact();
	contact.setPhoneNumber(phoneNumber);
	contact.setPerson(person);
    }

    @Atomic
    private static void showPerson(Person person) {
	System.out.printf("Transaction number %d\n", Transaction.current().getNumber());
	System.out.println("Show person");
	System.out.println("\tName: " + person.getName());
	if (person.hasContact()) {
	    System.out.println("\tContact:" + person.getContact().getPhoneNumber());
	}
    }

    @Atomic(speculativeReadOnly = false)
    private static Person addPerson(String name) {
	System.out.printf("Transaction number %d\n", Transaction.current().getNumber());
	System.out.printf("Add person %s \n", name);
	final HelloWorldApplication app = FenixFramework.getRoot();
	Person person = new Person(name, app);
	System.out.printf("Person %s with oid %s created.\n", name, person.getExternalId());
	return person;
    }

    @Atomic(speculativeReadOnly = false)
    private static void delete(Person person) {
	System.out.printf("Transaction number %d\n", Transaction.current().getNumber());
	System.out.printf("Delete person %s\n", person.getName());
	person.delete();
	System.out.printf("Person %s deleted.\n", person.getName());
    }

    @Atomic(speculativeReadOnly = false)
    private static void reconnectPersonToRoot(Person person) {
	System.out.printf("Transaction number %d\n", Transaction.current().getNumber());
	System.out.printf("reset root to person %s\n", person.getName());
	person.setApp((HelloWorldApplication) FenixFramework.getRoot());
    }

}