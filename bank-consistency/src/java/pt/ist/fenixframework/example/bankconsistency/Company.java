package pt.ist.fenixframework.example.bankconsistency;

import pt.ist.fenixframework.pstm.consistencyPredicates.ConsistencyPredicate;

public class Company extends Company_Base {

    public Company() {
	super();
    }

    public Company(String companyName) {
	super();
	setApplication(BankConsistencyApplication.getInstance());
	setCompanyName(companyName);
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + " [" + getIdInternal() + "] " + getCompanyName();
    }

    @ConsistencyPredicate
    public boolean checkHasOneClientNamedBill() {
	boolean foundBill = false;
	for (Client client : getClients()) {
	    if (client.getName().equals("Bill")) {
		if (!foundBill) {
		    foundBill = true;
		} else {
		    //More than one Bill found!
		    return false;
		}
	    }

	}
	return foundBill;
    }
}
