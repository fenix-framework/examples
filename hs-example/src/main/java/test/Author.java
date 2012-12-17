package test;

import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

@Indexed
public class Author extends Author_Base {

    public  Author() {
        super();
    }

    public Author(String name) {
        this();
        setName(name);
    }

    @Field
    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public String toString() {
        return "Author " + getName();
    }

    @Override
    @ContainedIn
    public java.util.Set<test.Book> getBooks() {
        return super.getBooks();
    }
}
