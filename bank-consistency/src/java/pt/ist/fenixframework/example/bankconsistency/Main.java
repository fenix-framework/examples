package pt.ist.fenixframework.example.bankconsistency;

import jvstm.Atomic;
import jvstm.cps.ConsistencyException;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.example.bankconsistency.BankConsistencyApplication.DomainPrinter;
import pt.ist.fenixframework.pstm.DomainFenixFrameworkRoot;
import pt.ist.fenixframework.pstm.DomainMetaClass;
import pt.ist.fenixframework.pstm.consistencyPredicates.DomainConsistencyPredicate;

public class Main {
    public static void main(final String[] args) {
	Config config = new Config() {
	    {
		domainModelPath = "/bank-consistency.dml";
		dbAlias = "//localhost:3306/bankconsistency";
		dbUsername = "root";
		dbPassword = "";
		updateRepositoryStructureIfNeeded = true;
		createRepositoryStructureIfNotExists = true;
		rootClass = BankConsistencyApplication.class;
		errorfIfDeletingObjectNotDisconnected = true;
		errorIfChangingDeletedObject = true;
		canCreateDomainMetaObjects = true;
	    }
	};
	FenixFramework.initialize(config);

	try {

	    createThingsAnimalsAndDogs();

	    //editFenixFrameworkDomainIllegaly();

	    populateDomainConsistent();

	    //createNewClientConsistent();

	    //changeClientNameConsistent();

	    //removeAccountConsistent();

	    //createDOsInconsistent();

	    //makeDomainInconsistent();

	    //createConsistentDOThatMakesOtherDOsInconsistent();

	    //removeDOThatMakesOtherDOsInconsistent();

	} catch (Error ex) {
	    if (ex.getCause() instanceof ConsistencyException) {
		ex.getCause().printStackTrace();
	    } else {
		ex.printStackTrace();
	    }
	} finally {
	    printDomain();
	}
    }

    @Atomic
    private static void createThingsAnimalsAndDogs() {
	Thing thing = new Thing();
	thing.setSize(3);
	Animal animal1 = new Animal();
	animal1.setAge(3);
	animal1.setSize(50);
	Animal animal2 = new Animal();
	animal2.setAge(3);
	animal2.setSize(50);
	Dog dog1 = new Dog();
	dog1.setName("boby");
	dog1.setAge(3);
	dog1.setSize(3);
	Dog dog2 = new Dog();
	dog2.setName("boby");
	dog2.setAge(3);
	dog2.setSize(3);
	Dog dog3 = new Dog();
	dog3.setName("boby");
	dog3.setAge(3);
	dog3.setSize(3);
    }

    @Atomic
    private static void editFenixFrameworkDomainIllegaly() {
	DomainMetaClass accountMetaClass = DomainFenixFrameworkRoot.getInstance().getDomainMetaClass(
		Account.class);
	DomainConsistencyPredicate testPredicate = null;
	for (DomainConsistencyPredicate predicate : accountMetaClass.getDeclaredConsistencyPredicates()) {
	    //accountMetaClass.removeDeclaredConsistencyPredicates(predicate);
	}
    }

    @Atomic
    private static void populateDomainConsistent() {
	Client clientGordon = new Client("Gordon");
	new ClientInfo(clientGordon, "1");
	Account accountGordon = new Account();
	accountGordon.setBalance(70);
	accountGordon.setClosed(false);
	clientGordon.addAccounts(accountGordon);

	Client clientZoey = new Client("Zoey");
	new ClientInfo(clientZoey, "2");
	Account accountZoeyA = new Account(clientZoey, 0);
	accountZoeyA.close();
	accountZoeyA.setDescription("This account is closed but consistent.");
	Account accountZoeyB = new Account(clientZoey, 30);

	Client clientBill = new Client("Bill");
	new ClientInfo(clientBill, "3");
	Account accountBillA = new Account(clientBill, 0);
	Account accountBillB = new Account(clientBill, 2000);
	Account accountBillC = new Account(clientBill, -1999);
	accountBillA.setDescription("0 account");
	accountBillB.setDescription("2k account");
	accountBillC.setDescription("negative account");

	Company companyMicrosoft = new Company("Microsoft");
	companyMicrosoft.addClients(clientBill);

	Company companyGoogle = new Company("Google");
	companyGoogle.addClients(clientBill);
	companyGoogle.addClients(clientZoey);
	companyGoogle.addClients(clientGordon);
    }

    @Atomic
    private static void createDOsInconsistent() {
	//new Client("Nick");

	//Client clientVick = new Client("Vick");
	//new ClientInfo(clientVick, null);

	//Client clientRick = new Client("Rick");
	//new ClientInfo(clientRick, "");

	//Client clientFrancis = new Client("Francis");
	//new ClientInfo(clientFrancis, "4");
	//Account accountFrancis = new Account(clientFrancis, 1);
	//accountFrancis.close();
	//accountFrancis.setDescription("This account is inconsistent!");

	//Client clientLouis = new Client("Louis");
	//new ClientInfo(clientLouis, "5");
	//Account accountLouisA = new Account(clientLouis, -20);
	//Account accountLouisB = new Account(clientLouis, 19);
	//accountLouisB.setDescription("This account has an inconsistent Client!");

	//Company companyApple = new Company("Apple");
	//Client clientElvis = new Client("Elvis");
	//new ClientInfo(clientElvis, "7");
	//companyApple.addClients(clientElvis);

	//Company companyApple = new Company("Apple");
	//Client clientBill1 = new Client("Bill");
	//new ClientInfo(clientBill1, "10");
	//Client clientBill2 = new Client("Bill");
	//new ClientInfo(clientBill2, "11");
	//companyApple.addClients(clientBill1);
	//companyApple.addClients(clientBill2);
    }

    @Atomic
    private static void makeDomainInconsistent() {
	BankConsistencyApplication app = BankConsistencyApplication.getInstance();
	for (Client client : app.getClients()) {
	    if (client.getName().equals("Bill")) {
		//client.setClientInfo(null);

		//client.getClientInfo().setIdNumber("");

		for (Account account : client.getAccounts()) {
		    String description = account.getDescription();
		    if (description != null && description.equals("2k account")) {
			//account.withdraw(2);
		    }

		    if (description != null && description.equals("negative account")) {
			//account.close();
		    }
		}

		//client.setName("Billy");
	    }

	    if (client.getName().equals("Gordon")) {
		//client.setName("Bill");
	    }
	}
    }

    @Atomic
    private static void createConsistentDOThatMakesOtherDOsInconsistent() {
	BankConsistencyApplication app = BankConsistencyApplication.getInstance();
	for (Client client : app.getClients()) {
	    if (client.getName().equals("Bill")) {
		//client.setClientInfo(null);
		//new ClientInfo(client, "");

		//new Account(client, -2);
	    }
	}

	for (Company company : app.getCompanies()) {
	    //Client newClientBill = new Client("Bill");
	    //new ClientInfo(newClientBill, "12");
	    //company.addClients(newClientBill);

	    break;
	}
    }

    @Atomic
    private static void removeDOThatMakesOtherDOsInconsistent() {
	BankConsistencyApplication app = BankConsistencyApplication.getInstance();
	for (Client client : app.getClients()) {
	    if (client.getName().equals("Bill")) {
		ClientInfo billClientInfo = client.getClientInfo();
		//client.setClientInfo(null);
		//billClientInfo.delete();

		for (Account account : client.getAccounts()) {
		    String description = account.getDescription();
		    if (description != null && description.equals("2k account")) {
			//account.removeClient();
			//account.removeApplication();
			//account.delete();
		    }

		    //account.removeClient();
		    //account.removeApplication();
		    //account.delete();
		}

		for (Company company : client.getCompanies()) {
		    //company.removeClients(client);
		}

		//billClientInfo.removeClient();
		//billClientInfo.delete();
		//client.removeApplication();
		//client.delete();
	    }
	}
    }

    @Atomic
    private static void createNewClientConsistent() {
	Client clientFrancis = new Client("Francis");
	new ClientInfo(clientFrancis, "6");
    }

    @Atomic
    private static void changeClientNameConsistent() {
	BankConsistencyApplication app = BankConsistencyApplication.getInstance();
	for (Client client : app.getClients()) {
	    if (client.getName().equals("Zoey")) {
		client.setName("Zoeyy");
	    }
	}
    }

    @Atomic
    private static void removeAccountConsistent() {
	BankConsistencyApplication app = BankConsistencyApplication.getInstance();
	for (Client client : app.getClients()) {
	    if (client.getName().equals("Zoeyy")) {
		for (Account account : client.getAccounts()) {
		    account.removeApplication();
		    account.removeClient();
		    account.delete();
		}
	    }
	}
    }

    @Atomic
    private static void printDomain() {
	DomainPrinter.printDomain();
    }
}
