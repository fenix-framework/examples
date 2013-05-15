package pt.ist.fenixframework.example.cluster;

public class Counter extends Counter_Base {
    
    public Counter() {
        super();
    }

    public int getAndInc() {
        int initial = getValue();
        setValue(initial + 1);
        return initial;
    }
    
}
