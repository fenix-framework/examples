package pt.ist.fenixframework.example.bankconsistency;

import jvstm.cps.ConsistencyPredicate;

public class Client extends Client_Base {

    public Client() {
	super();
    }

    public Client(String name) {
	super();
	setApplication(BankConsistencyApplication.getInstance());
	setName(name);
    }

    public void delete() {
	deleteDomainObject();
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + " [" + getIdInternal() + "] " + getName();
    }

    public int getTotalBalance() {
	int totalBalance = 0;
	for (Account account : getAccounts()) {
	    totalBalance += account.getBalance();
	}
	return totalBalance;
    }

    @ConsistencyPredicate
    public boolean checkTotalBalancePositive() {
	return getTotalBalance() >= 0;
    }

    @ConsistencyPredicate
    public boolean allowCreation() {
	return true;
    }

    @ConsistencyPredicate
    public boolean checkHasIdNumber() {
	return !getClientInfo().getIdNumber().isEmpty();
    }

    /*@ConsistencyPredicate
    public boolean performIllegalWrite() {
    setName("");
    return true;
    }*/
}
