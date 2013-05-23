package pt.ist.fenixframework.example.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class Increment {

    private static final Logger logger = LoggerFactory.getLogger(Increment.class);

    public static void main(final String[] args) {
        try {
            ensureCounter();
            while (true) {
                logger.info("Increment loop");
                incCounter();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    logger.warn("Interrupted.", e);
                    break;
                }
            }
        } finally {
            FenixFramework.shutdown();
        }
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private static void ensureCounter() {
        Counter counter = FenixFramework.getDomainRoot().getCounter();
        if (counter == null) {
            logger.info("Create counter");
            FenixFramework.getDomainRoot().setCounter(new Counter());
        }
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private static void incCounter() {
        logger.info("Increment tx");
        Counter counter = FenixFramework.getDomainRoot().getCounter();
        System.out.println("start value=" + counter.getAndInc());
//        for (int i = 0; i < 100; i++) {
//            new Counter();
//        }
        FenixFramework.getDomainRoot().addClock(new Counter());
    }

}
