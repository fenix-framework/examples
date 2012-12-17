package test;

import java.util.*;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainRoot;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.hibernatesearch.HibernateSearchSupport;

import org.apache.lucene.search.Query;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.engine.spi.EntityInfo;
import org.hibernate.search.query.engine.spi.HSQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static final int AUTH_COUNT = 1000;
    public static final int PUB_COUNT = 50;
    public static final int BOOK_COUNT = AUTH_COUNT * 10;

    public static void main(String[] args) {
        try {
            initDomain();
            doQueries();
            modifyDomain();
            doMoreQueries();
        } finally {
            FenixFramework.shutdown();
        }
    }

    @Atomic
    public static void initDomain() {
        DomainRoot domainRoot = FenixFramework.getDomainRoot();

        // Authors
        for (int i = 0; i < AUTH_COUNT; i++) {
            domainRoot.addTheAuthors(new Author("Auth" + i));
        }

        // Publishers
        for (int i = 0; i < PUB_COUNT; i++) {
            domainRoot.addThePublishers(new Publisher("Pub" + i));
        }

        // Books
        for (int i = 0; i < BOOK_COUNT; i++) {
            Book book = null;

            switch (i%3) {
                case 0:
                    book = new Book("Book" + i);
                    break;
                case 1:
                    book = new ComicBook("Book" + i);
                    break;
                case 2:
                    book = new ScifiBook("Book" + i);
                    break;
            }

            domainRoot.addTheBooks(book);
        }
    }

    @Atomic
    public static void doQueries() {
        logger.debug("Doing example queries. Configured " + AUTH_COUNT + " authors, " + PUB_COUNT
                + " publishers, and " + BOOK_COUNT + " books");

        logger.debug("Find Book300: " + performQuery(Book.class, "bookName", "book300"));

        logger.debug("Find Book3*3: " + performWildcardQuery(Book.class, "bookName", "book3*3"));

        logger.debug("Find ScifiBook3*3: " + performWildcardQuery(ScifiBook.class, "bookName", "book3*3"));

        logger.debug("Find Scifi Books by Auth0: " + performQuery(ScifiBook.class, "authors.id",
                getAuthorByName("Auth0").getExternalId()));
    }

    @Atomic
    public static void modifyDomain() {
        logger.debug("Adding books to Auth0");

        Author auth0 = getAuthorByName("Auth0");
        for (int i = 0; i < 20; i++) {
            auth0.addBooks(getBookByName("Book" + i));
        }
    }

    @Atomic
    public static void doMoreQueries() {
        logger.debug("Find Scifi Books by Auth0: " + performQuery(ScifiBook.class, "authors.id",
                getAuthorByName("Auth0").getExternalId()));
    }

    // See
    // https://docs.jboss.org/hibernate/search/4.2/reference/en-US/html_single/#section-building-lucene-queries
    // for more examples on how to build queries

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> performQuery(Class<T> cls, String field, String queryString) {
        ArrayList<T> matchingObjects = new ArrayList<T>();

        QueryBuilder qb = HibernateSearchSupport.getSearchFactory().buildQueryBuilder().forEntity(cls).get();
        Query query = qb.keyword().onField(field).matching(queryString).createQuery();
        HSQuery hsQuery = HibernateSearchSupport.getSearchFactory().createHSQuery().luceneQuery(query)
                .targetedEntities(Arrays.<Class<?>>asList(cls));
        hsQuery.getTimeoutManager().start();
        for (EntityInfo ei : hsQuery.queryEntityInfos()) {
            matchingObjects.add((T) FenixFramework.getDomainObject((String) ei.getId()));
        }
        hsQuery.getTimeoutManager().stop();

        return matchingObjects;
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> performWildcardQuery(Class<T> cls, String field, String queryString) {
        ArrayList<T> matchingObjects = new ArrayList<T>();

        QueryBuilder qb = HibernateSearchSupport.getSearchFactory().buildQueryBuilder().forEntity(cls).get();
        Query query = qb.keyword().wildcard().onField(field).matching(queryString).createQuery();
        HSQuery hsQuery = HibernateSearchSupport.getSearchFactory().createHSQuery().luceneQuery(query)
                .targetedEntities(Arrays.<Class<?>>asList(cls));
        hsQuery.getTimeoutManager().start();
        for (EntityInfo ei : hsQuery.queryEntityInfos()) {
            matchingObjects.add((T) FenixFramework.getDomainObject((String) ei.getId()));
        }
        hsQuery.getTimeoutManager().stop();

        return matchingObjects;
    }

    @Atomic
    public static Author getAuthorByName(String authorName) {
        DomainRoot domainRoot = FenixFramework.getDomainRoot();
        for (Author author : domainRoot.getTheAuthors()) {
            if (author.getName().equals(authorName)) {
                return author;
            }
        }
        return null;
    }

    @Atomic
    public static Book getBookByName(String bookName) {
        DomainRoot domainRoot = FenixFramework.getDomainRoot();
        for (Book book : domainRoot.getTheBooks()) {
            if (book.getBookName().equals(bookName)) {
                return book;
            }
        }
        return null;
    }

    @Atomic
    public static Publisher getPublisherByName(String publisherName) {
        DomainRoot domainRoot = FenixFramework.getDomainRoot();
        for (Publisher publisher : domainRoot.getThePublishers()) {
            if (publisher.getPublisherName().equals(publisherName)) {
                return publisher;
            }
        }
        return null;
    }
}
