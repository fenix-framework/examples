## Adapting the original TPC-W implementation to run with the fenix-framework

In this section we describe the process we followed to adapt the TPC-W java
implementation to run with the fenix-framework.  This part of this document is
mostly useful to introduce the reader to the aspects of the original
implementation that had to be changed.  It was written while the work was
being executed and it provides an overview of the changes needed to perform.

### Compiling and deploying the original TPC-W implementation

It is mostly straightforward given the indications in the download site. The
only problem encountered regards the use of a method named "assert".

> as of release 1.4, 'assert' is a keyword, and may not be used as an
  identifier (use -source 1.3 or lower to use 'assert' as an identifier)

The solution was to change the method name to something else (assert_v2).  It
was easy because we had the source code.

### Running the original benchmark

After populating the database, we tried to run the benchmark through the
provided script rbe.sh. However, the code didn't work out-of-the-box with
mysql.  There were some problems regarding the SQL instructions that we had to
correct.

#### Problem 1: web server location

Our web server was located on port 8080 and we had to change the rbe.sh script
to account for that, even though we had already defined it in the
tpcw.properties file.


#### Problem 2: insert with sub-select

The sql query defined in property `sql.createEmptyCart.insert` could not be used
with MySQL.

    sql.createEmptyCart.insert="INSERT into shopping_cart (sc_id, sc_time) VALUES ((SELECT COUNT(*) FROM shopping_cart),CURRENT_TIMESTAMP)"

MySQL gave the following error:

    You can't specify target table 'shopping_cart' for update in FROM clause

This simply means that it is not possible to count the rows of a table
(through a sub-select) while inserting data into that same table. To solve
this problem we had to change the query. We decided to create a new property

    sql.createEmptyCart.insert.v2="INSERT into shopping_cart (sc_id, sc_time) VALUES (?,CURRENT_TIMESTAMP)"

and change the code to first calculate the row count using another query
already available:

    sql.createEmptyCart="SELECT COUNT(*) FROM shopping_cart"

To our amazement we found that this two-step query was already done in the
code, but it wasn't being used. We just needed to change to the new query and
bind the count value. The was changed in TPCW_Database.std.java in method
`createEmptyCart()` from

    PreparedStatement insert_cart = con.prepareStatement
        (@sql.createEmptyCart.insert@);

to

    PreparedStatement insert_cart = con.prepareStatement
        (@sql.createEmptyCart.insert.v2@);
    insert_cart.setInt(1, SHOPPING_ID);


The `SHOPPING_ID` was obtained immediately before with the
`sql.createEmptyCart` query.

The two queries are within the same transaction and simultaneously are within
a `synchronized (Cart.class)` block.  Thus, we expect that introducing the two
queries will not lead to concurrency problems.


#### Problem 3: java.sql.SQLException: Illegal operation on empty result set

This problem only occurred because we were giving the incorrect parameters to
the test client.  We're leaving it here as a reference to help solve this
problem faster should it arise again in the future.  In our case we were
giving a number of customers (-CUST) greater than the real number of customers
actually stored in the database. :-(

### Modelling tpc-w in the fenix-framework

 * Dates
 * other issues/decisions

#### Class name Orders vs. Order

*Order* is an SQL reserved keyword.  Thus, the concept of an Order is modelled
 in a class name `Orders` instead of `Order` (which would be the normal case).


#### Books related to other books (items)

Each book (item) is related to five other books.  "At most five" or "exactly
five" must be checked with the TPC-W specification.  The implementation that
we used as the basis, always sets 5 related books.

We can model this in the DML in two ways: either define five 1-to-1 relations
between book and book or define a single 1-N relation between book and book.
Ideally we would like to model a 1-to-5 (or a 5-to-5 if the semantics is
"exactly 5"), but this is not yet possible.  For now, we chose the first
approach to keep our interface as similar to the original tpc-w implementation
as possible, but this can easily be changed in the future.

#### Persistent shopping cart

The TPC-W specification requires a persistent shopping cart even though the
cart is not described as a domain concept.  We implemented the cart as a
domain concept, which was also the solution in the original implementation.

### Server re-implementation

Semantics of each public method in TPCW_Database class:


| **Method name**            | **Description**                                      |
| :------                    | ------                                               |
|String[] getName(int c_id)| Return customer's first and last name given his c_id |
|Book getBook(int i_id)| Return a book (with author information) given the book i_id|
|Customer getCustomer(String UNAME)| Get the customer with the given username or null in case of any error. |
|Vector doSubjectSearch(String search_key)| Get a list of Books sorted by title for a given subject (search_key), limited to 50 elements|
|Vector doTitleSearch(String search_key)| Get a list of Books for title similar to the search_key (soundex) sorted by title, limited to 50 elements |
|Vector doAuthorSearch(String search_key)| Get books from an author whose last name sounds like search_key (using soundex()) sorted by title, limit to 50|
|Vector getNewProducts(String subject)| Get a list of ShortBooks for a given subject sorted by publication date (descending) and title, limited to 50 elements |
|Vector getBestSellers(String subject)| Get a list of most ordered ShortBooks for a given subject, considering only the most recent orders (last 3333), sorted by order quantity (descending), limited to 50 elements |
|void getRelated(int i_id, Vector i_id_vec, Vector i_thumbnail_vec)| Get pairs (i_id, i_thumbnail) for all items related with the given i_id |
|void adminUpdate(int i_id, double cost, String image, String thumbnail)| Update an item (book) with a new cost, image and thumbnail. Also set the 5 related books using the following rule: out of the latest 10000 orders, pick the clients that have ordered this book. Of those, sum all the quantities they have ever ordered of other books and pick the top 5 most ordered.  Set them as the related books.  If there are less than 5 that match, choose random books to fill. |
|String GetUserName(int C_ID)| Return a user's username given the user id|
|String GetPassword(String C_UNAME)| Return a user's password given the username |
|Order GetMostRecentOrder(String c_uname, Vector order_lines)| Get the most recent order for a given customer (or null) if none. The order_lines DTOs are returned in the vector parameter order_lines |
| **Shopping Cart code below** | |
|int createEmptyCart()| Create a new empty shopping cart |
|Cart doCart(int SHOPPING_ID, Integer I_ID, Vector ids, Vector quantities)| Top-level shopping cart processing. Uses: addItem, refreshCart, addRandom, resetCart. |
|void addItem(int SHOPPING_ID, int I_ID)| Adds an item to the shopping cart in quantity 1 or increases existing item's quantity by 1. |
|void refreshCart(int SHOPPING_ID, Vector ids, Vector quantities)| Update the quantities in the shopping cart. Remove when quantities are 0. |
|void addRandomItemToCartIfNecessary_shouldGoAway(Connection con, int SHOPPING_ID)| If the shopping cart is empty add a random item. |
|void resetCartTime(pt.ist.fenixframework.example.tpcw.domain.ShoppingCart cart)| Reset the shopping cart to the current time. |
|Cart getCart(int SHOPPING_ID, double c_discount)| Get the shopping cart and apply the given discount |
| **Customer / Order code below** | |
|void refreshSession(int C_ID)| Update the customer's session information (login time and expiration time). |
|Customer createNewCustomer(Customer cust)| Create a new customer with the given the customer information |
|BuyConfirmResult doBuyConfirm(int shopping_id, int customer_id, String cc_type, long cc_number, String cc_name, Date cc_expiry, String shipping)| Execute the purchase process (use existing address) |
|BuyConfirmResult doBuyConfirm(int shopping_id, int customer_id, String cc_type, long cc_number, String cc_name, Date cc_expiry, String shipping, String street_1, String street_2, String city, String state, String zip, String country)| Execute the purchace process (using a new address) |
|double getCDiscount(int c_id)| Return the customer's discount amount. (Changed to private method) |
|int getCAddrID(int c_id)| Return the customer's address id. (Changed to private method) |
|int getCAddr(int c_id)| Return the customer's address id. This method does the same as getCAddrID(). (Changed to private method) |
|void enterCCXact(int o_id, String cc_type, long cc_number, String cc_name, Date cc_expiry, double total, int ship_addr_id)| Creates a new credit cart transaction. (Changed to private method) |
|void clearCart(int shopping_id)| Empties the shopping cart contents. (Changed to private method) |
|int enterAddress(String street1, String street2, String city, String state, String zip, String country)| Creates a new address if it doesn't already exit and returns it (originally returned its ID) (Changed to private method) |
|int enterOrder(int customer_id, Cart cart, int ship_addr_id, String shipping, double c_discount)| Places a new Order based on an existing shopping cart. Ajusts the available stock accordingly. (Changed to private method) |
|void addOrderLine(int ol_id, int ol_o_id, int ol_i_id, int ol_qty, double ol_discount, String ol_comment)| Add a new order line to an existing order. (Changed to private method) |
|int getStock(int i_id)| Get the stock for a given item id, zero by default if an error occurs. (Changed to private method) |
|void setStock(int i_id, int new_stock)| Set the stock value for an item with the given item id. (Changed to private method) |
|void verifyDBConsistency()| (Changed to private method) |


#### Strategy for reusing as much server code as possible

The TPC-W server code is composed by servlet classes, a class with the
database queries and domain/DTO classes to represent the information fetched
from the database.  We can reuse the servlet classes trivially, we simply have
to re-implement the database queries to execute similar queries via the object
system. We have reused the original domain classes/DTOs by creating our own
DTOs that inherit from them.  This was needed because the exiting classes'
constructors only received ResultSets. Thus, we created subclasses that set
the data from our domain objects.
