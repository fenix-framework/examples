package pt.ist.fenixframework.example.cluster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.FenixFramework;

public class Print {

    private static final Logger logger = LoggerFactory.getLogger(Print.class);

    public static void main(final String[] args) {
        try {
            while (true) {
                printValue();
                try {
                    logger.info("Print loop");
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

    @Atomic
    private static void printValue() {
        Counter counter = FenixFramework.getDomainRoot().getCounter();
        Counter otherCounter = FenixFramework.getDomainRoot().getOtherCounter();
        System.out.println("Counter = " + (counter == null ? "N/A" : counter.getValue()) + "; OtherCounter = " + (otherCounter == null ? "N/A" : otherCounter.getValue()));
    }

}
