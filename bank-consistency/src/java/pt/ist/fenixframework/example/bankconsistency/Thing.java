package pt.ist.fenixframework.example.bankconsistency;

import pt.ist.fenixframework.pstm.consistencyPredicates.ConsistencyPredicate;

public class Thing extends Thing_Base {
    
    public  Thing() {
        super();
    }
    
    @ConsistencyPredicate
    private boolean checkSize() {
	return getSize() != null;
    }
}
