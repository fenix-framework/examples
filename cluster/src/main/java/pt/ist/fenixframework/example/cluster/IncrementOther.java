package pt.ist.fenixframework.example.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class IncrementOther {

    private static final Logger logger = LoggerFactory.getLogger(IncrementOther.class);

    public static void main(final String[] args) {
        try {
            ensureOtherCounter();
            while (true) {
                logger.info("Increment loop");
                incOtherCounter();
                try {
                    Thread.sleep(1000);
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
    private static void ensureOtherCounter() {
        Counter otherCounter = FenixFramework.getDomainRoot().getOtherCounter();
        if (otherCounter == null) {
            logger.info("Create otherCounter");
            FenixFramework.getDomainRoot().setOtherCounter(new Counter());
        }
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    private static void incOtherCounter() {
        logger.info("Increment tx");
        Counter otherCounter = FenixFramework.getDomainRoot().getOtherCounter();
        System.out.println("start value=" + otherCounter.getAndInc());
    }

}
