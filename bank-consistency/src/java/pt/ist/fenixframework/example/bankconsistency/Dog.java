package pt.ist.fenixframework.example.bankconsistency;

import pt.ist.fenixframework.pstm.consistencyPredicates.ConsistencyPredicate;

public class Dog extends Dog_Base {
    
    public  Dog() {
        super();
    }
    
    @Override
    @ConsistencyPredicate
    public boolean checkAge() {
	return getAge() <= 40;
    }

    @Override
    @ConsistencyPredicate
    public boolean checkSize() {
	return getSize() <= 10;
    }

}
