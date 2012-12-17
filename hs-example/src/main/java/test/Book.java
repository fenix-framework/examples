package test;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

@Indexed
public class Book extends Book_Base {

    public  Book() {
        super();
    }

    public Book(String name) {
        this();
        setBookName(name);
    }

    @Field
    @Override
    public String getBookName() {
        return super.getBookName();
    }

    @Field
    @Override
    public double getPrice() {
        return super.getPrice();
    }

    @Override
    public String toString() {
        return "Book " + getBookName();
    }

    @Override
    @IndexedEmbedded
    public test.Publisher getPublisher() {
        return super.getPublisher();
    }

    @Override
    @IndexedEmbedded
    public java.util.Set<test.Author> getAuthors() {
        return super.getAuthors();
    }
}
