package pt.ist.fenixframework.example.bankconsistency;

import pt.ist.fenixframework.pstm.consistencyPredicates.ConsistencyPredicate;

public class Animal extends Animal_Base {
    
    public  Animal() {
        super();
    }

    @ConsistencyPredicate
    public boolean checkSize() {
	return getSize() <= 50;
    }

    @ConsistencyPredicate
    public boolean checkAge() {
	return (0 <= getAge()) && (getAge() <= 400);
    }
}
