package pt.ist.fenixframework.example.suspend;

import java.util.Scanner;

import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;
import pt.ist.fenixframework.example.suspend.domain.App;
import pt.ist.fenixframework.example.suspend.domain.Person;

public class SuspendTest {

    private static final Logger logger = LoggerFactory.getLogger(SuspendTest.class);

    public static void main(final String[] args) {
        try {
            logger.info("Will start the repl");
            new SuspendTest().repl();
        } finally {
            FenixFramework.shutdown();
        }
    }

    private TransactionManager tm;
    private Transaction tx;
    private App app;

    SuspendTest() {
        tm = FenixFramework.getTransactionManager();
        tx = null;
    }

    @Atomic
    private void init() {
        logger.info("app is null");
        App app = new App();
        logger.info("setting app {} on root {}", app.getExternalId(), FenixFramework.getDomainRoot().getExternalId());
        FenixFramework.getDomainRoot().setApp(app);
        this.app = app;
    }

    private void repl() {
        init();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            String cmd = scanner.next();

            try {
                if (cmd.equals("quit")) {
                    rollback();
                    return;
                } else if (cmd.equals("list")) {
                    listPeople();
                } else if (cmd.equals("add")) {
                    addPerson(scanner.nextLine().trim());
                } else if (cmd.equals("done")) {
                    commit();
                } else if (cmd.equals("cancel")) {
                    rollback();
                } else if (cmd.equals("rename")) {
                    rename(scanner.nextLine().trim(), scanner.nextLine().trim());
                } else if (cmd.equals("show")) {
                    showPerson(scanner.nextLine().trim());
                } else {
                    System.out.println("Unknown command: " + cmd);
                }
            } catch (Exception e) {
                System.out.println("Error during processing of command: " + e.getMessage());
            }
        }
    }

    private void resumeTx() {
        if (tx == null) {
            try {
                tm.begin();
            } catch (Exception e) {
                logger.error("Begin transaction failed {}", e);
                throw new RuntimeException("Aborting operation, because Begin failed");
            }
        } else {
            boolean resumed = false;
            try {
                tm.resume(tx);
                resumed = true;
            } catch (Exception e) {
                logger.error("Resume transaction failed {}", e);
                throw new RuntimeException("Aborting operation, because resume failed");
            } finally {
                if (!resumed) {
                    rollbackTx();
                }
            }
        }
    }

    private void listPeople() {
        resumeTx();

        for (Person p : app.getPersonSet()) {
            System.out.println( p.getName() + " (externalId= " + p.getExternalId() + " , " + p + " )");
        }

        suspendTx();
    }

    private void addPerson(String name) {
        resumeTx();
        app.addPerson(new Person(name));
        suspendTx();
    }

    private void rename(String from, String to) {
        resumeTx();

        for (Person p : app.getPersonSet()) {
            if (p.getName().equals(from)) {
                p.setName(to);
            }
        }

        suspendTx();
    }

    private void showPerson(String externalId) {
        resumeTx();

        try {
            Person p = FenixFramework.getDomainObject(externalId);
            System.out.println( p.getName() + " (externalId= " + p.getExternalId() + " , " + p + " )");
        } catch (Exception e) {
            System.out.println("Can't show person with externalid = " + externalId + " : " + e);
        }

        suspendTx();
    }

    private void commit() {
        resumeTx();
        boolean finished = false;
        try {
            tm.commit();
            finished = true;
        } catch (Exception e) {
            logger.error("Commit transaction failed {}", e);
            throw new RuntimeException("Aborting operation, because commit failed");
        } finally {
            if (!finished) {
                rollbackTx();
            }
            tx = null;
        }
    }

    private void rollback() {
        resumeTx();
        rollbackTx();
    }

    private void rollbackTx() {
        try {
            tm.rollback();
        } catch (Exception e) {
            logger.error("Rollback transaction failed {}", e);
            throw new RuntimeException("Aborting operation, because rollback failed");
        } finally {
            tx = null;
        }
    }

    private void suspendTx() {
        try {
            tx = tm.suspend();
        } catch (SystemException e) {
            logger.error("Suspend transaction failed {}", e);
            throw new RuntimeException("Aborting operation, because suspend failed");
        }
    }
}
