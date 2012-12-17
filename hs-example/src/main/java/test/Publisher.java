package test;

import org.hibernate.search.annotations.ContainedIn;

public class Publisher extends Publisher_Base {

    public  Publisher() {
        super();
    }

    public Publisher(String publisherName) {
        this();
        setPublisherName(publisherName);
    }

    @Override
    public String toString() {
        return "Publisher " + getPublisherName();
    }

    @Override
    @ContainedIn
    public java.util.Set<test.Book> getBooksPublished() {
        return super.getBooksPublished();
    }
}
