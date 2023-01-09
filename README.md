# Implementing an ERD with JPA

## Learning Goals

- Implement an Entity Relationship Model using Java Persistence API

## Code Along

## Introduction

In this lesson, we review the process of implementing an entity relationship model
using Java Persistence API (JPA).  We will implement Java classes for the following
entity relationship model:

![many-to-many version1](https://curriculum-content.s3.amazonaws.com/6036/java-mod-5-erd-tool/many_to_many_v1.png)

This version does not model the many-to-many relationship between `Book` and `Author`
as a separate entity in order to demonstrate the `@ManyToMany` annotation.

## Create a new project

We will create a new PostgreSQL database named `publishing_db`, along with
a new Java project named `jpa-publishing` to implement the publishing data model using JPA.

1. Open the PostgreSQL `pgAdmin` tool and create a new database named `publishing_db`.
2. In IntelliJ, create a new Maven Java project named `jpa-publishing`.     
   ![new jpa project](https://curriculum-content.s3.amazonaws.com/6036/java-mod-5-erd-to-jpa/newproject.png)
3. Add the PostgreSQL and Hibernate dependencies to `pom.xml`.  Make sure to press the "Load Maven Changes" icon.     

   ```text
   <?xml version="1.0" encoding="UTF-8"?>
   <project xmlns="http://maven.apache.org/POM/4.0.0"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   <modelVersion>4.0.0</modelVersion>
   
       <groupId>org.example</groupId>
       <artifactId>jpa-publishing</artifactId>
       <version>1.0-SNAPSHOT</version>
   
       <properties>
           <maven.compiler.source>11</maven.compiler.source>
           <maven.compiler.target>11</maven.compiler.target>
           <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
       </properties>
   
       <dependencies>
           <dependency>
               <groupId>org.postgresql</groupId>
               <artifactId>postgresql</artifactId>
               <version>42.5.1</version>
           </dependency>
           <dependency>
               <groupId>org.hibernate</groupId>
               <artifactId>hibernate-entitymanager</artifactId>
               <version>5.6.14.Final</version>
           </dependency>
       </dependencies>
   
   </project>
   ```   
   
4. Create a directory `META-INF` within `src/main/resources`.
5. Create a new file `persistence.xml` in the `META-INF` directory.    

   ```text
   <persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
                            http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
                version="2.0" xmlns="http://java.sun.com/xml/ns/persistence">
   
       <persistence-unit name="example" transaction-type="RESOURCE_LOCAL">
           <provider>org.hibernate.ejb.HibernatePersistence</provider>
           <properties>
               <!-- connect to database -->
               <property name="javax.persistence.jdbc.driver" value="org.postgresql.Driver" /> <!-- DB Driver -->
               <property name="javax.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/publishing_db" /> <!--DB URL-->
               <property name="javax.persistence.jdbc.user" value="postgres" /> <!-- DB User -->
               <property name="javax.persistence.jdbc.password" value="postgres" /> <!-- DB Password -->
               <!-- configure behavior -->
               <property name="hibernate.hbm2ddl.auto" value="create" /> <!-- create / create-drop / update / none -->
               <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQL94Dialect"/>
               <property name="hibernate.show_sql" value="true" /> <!-- Show SQL in console -->
               <property name="hibernate.format_sql" value="true" /> <!-- Show SQL formatted -->
           </properties>
       </persistence-unit>
   </persistence>
   ```   
   
6. Right-click on the `Main` class, select Refactor/Rename to rename the class as `JpaCreate`.
7. Right-click on `org.example` folder and add a new class `JpaRead`.
8. Right-click on `org.example` folder and add a new package `org.example.model`.
9. Right-click on the `model` package and add 4 new classes:  

   - Publisher
   - Address
   - Book
   - Author

The project structure should appear as shown:

![project structure](https://curriculum-content.s3.amazonaws.com/6036/java-mod-5-erd-to-jpa/project_structure.png)

## Implementing the entity classes

Note: Use IntelliJ to auto-generate the `toString()` method
along with accessor methods (`get` and `set`) for every instance
variable defined in the entity classes.

First we will implement the `Publisher` entity.  The primary key is `id`, while `name` should also be unique and not null:

```java
package org.example.model;

import javax.persistence.*;

@Entity
public class Publisher {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique=true, nullable=false)
    private String name;

    private String website;

    //getters, setters, toString
}
```

The `Address` entity requires city and country as non-null values.  The default value for `country`
is specified using an initializer as part of the variable declaration,
along with the`@Column` annotation to force the SQL constraint
in the database:

```java
package org.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Address {
   @Id
   @GeneratedValue
   private int id;

   private String street;

   @Column(nullable=false)
   private String city;

   private String state, zip;

   @Column(columnDefinition = "varchar(100) default 'United States'")
   private String country = "United States";

   //getters, setters, toString
}
```

The `Book` entity is as shown below:

```java
package org.example.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Book {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable=false)
    private String title;

    @Column(unique=true, nullable=false)
    private String isbn;

    @Column(name="publication_date")
    private LocalDate publicationDate;

    private int pages;

    @Column(columnDefinition = "integer default 1")
    private int edition = 1;

    //getters, setters, toString
}
```

Finally, edit the `Author` entity as shown:

```java
package org.example.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Author {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    //getters, setters, toString
}
```


## Persisting objects to the database

We haven't added the relationships between entities yet, but let's make sure our
entity classes and project configuration files are correct by creating instances of
the entity classes and persisting them to the database.

Edit `JpaCreate` to add the following code:

```java
package org.example;

import org.example.model.Address;
import org.example.model.Author;
import org.example.model.Book;
import org.example.model.Publisher;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.time.LocalDate;

public class JpaCreate {
   public static void main(String[] args) {
      // create Entities
      Publisher publisher1 = new Publisher();
      publisher1.setName("HarperCollins");
      publisher1.setWebsite("harpercollins.com");

      Publisher publisher2 = new Publisher();
      publisher2.setName("O'Reilly Media, Inc.");
      publisher2.setWebsite("oreilly.com");

      Address address1 = new Address();
      address1.setCity("New York");
      address1.setState("NY");

      Address address2 = new Address();
      address2.setCity("Sebastopol");
      address2.setState("CA");

      Author author1 = new Author();
      author1.setFirstName("Jory");
      author1.setLastName("John");

      Author author2 = new Author();
      author2.setFirstName("Pete");
      author2.setLastName("Oswald");

      Author author3 = new Author();
      author3.setFirstName("Shel");
      author3.setLastName("Silverstein");

      Author author4 = new Author();
      author4.setFirstName("Luciano");
      author4.setLastName("Ramalho");

      Book book1 = new Book();
      book1.setTitle("The Sour Grape");
      book1.setIsbn("9780063045415");
      book1.setPages(40);
      book1.setPublicationDate(LocalDate.of(2022,11,1));

      Book book2 = new Book();
      book2.setTitle("The Giving Tree");
      book2.setIsbn("9780060256654");
      book2.setPages(64);
      book2.setPublicationDate(LocalDate.of(2014,2,18));

      Book book3 = new Book();
      book3.setTitle("Fluent Python");
      book3.setIsbn("9781492056355");
      book3.setEdition(2);
      book3.setPages(790);
      book3.setPublicationDate(LocalDate.of(2022,4,1));

      Book book4 = new Book();
      book4.setTitle("Where the Sidewalk Ends");
      book4.setIsbn("9780060256678");
      book4.setPages(176);
      book4.setPublicationDate(LocalDate.of(2014, 2, 18));
      
      // create EntityManager
      EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example");
      EntityManager entityManager = entityManagerFactory.createEntityManager();

      // access transaction object
      EntityTransaction transaction = entityManager.getTransaction();

      // create and use transactions
      transaction.begin();

      //persist the objects
      entityManager.persist(publisher1);
      entityManager.persist(publisher2);
      entityManager.persist(address1);
      entityManager.persist(address2);
      entityManager.persist(author1);
      entityManager.persist(author2);
      entityManager.persist(author3);
      entityManager.persist(author4);
      entityManager.persist(book1);
      entityManager.persist(book2);
      entityManager.persist(book3);
      entityManager.persist(book4);

      transaction.commit();

      //close entity manager and factory
      entityManager.close();
      entityManagerFactory.close();
   }
}
```

1. Run `JpaCreate.main`.  If you see an error about connecting to the database, make sure you created the
   database named `publishing_db` using the `pgAdmin4` tool.
2. Use the `pgAdmin` query tool to query the tables:

```sql
SELECT * 
FROM PUBLISHER;
```

| id  | name                  | website            |
|-----|-----------------------|--------------------|
| 1   | HarperCollins         | harpercollins.com  |
| 2   | O'Reilly Media, Inc.  | oreilly.com        |


```sql
SELECT * 
FROM ADDRESS;
```

| id  | city       | country       | state  | street | zip  |
|-----|------------|---------------|--------|--------|------|
| 3   | New York   | United States | NY     | null   | null |
| 4   | Sebastopol | United States | CA     | null   | null |


```sql
SELECT * 
FROM AUTHOR;
```

| id  | first_name | last_name   |
|-----|------------|-------------|
| 5   | Jory       | John        |
| 6   | Pete       | Oswald      |
| 7   | Shel       | Silverstein |
| 8   | Luciano    | Ramalho     |


```sql
SELECT * 
FROM BOOK;
```

| id  | edition | isbn          | pages | publication_date | title                   |
|-----|---------|---------------|-------|------------------|-------------------------|
| 9   | 1       | 9780063045415 | 40    | 2022-11-01       | The Sour Grape          |
| 10  | 1       | 9780060256654 | 64    | 2014-02-18       | The Giving Tree         |
| 11  | 2       | 9781492056355 | 790   | 2022-04-01       | Fluent Python           |
| 12  | 1       | 9780060256678 | 176   | 2014-02-18       | Where the Sidewalk Ends |


## Implementing The One-To-One Relationship with JPA

When defining a relationship between two entities,
a common task is identifying the **owning side** of the relationship.
The entity on the owning side is referred to as the  **relationship owner** 
and a foreign key referencing the entity on the **non-owning**
side will be stored in their database table.

In the one-to-one relationship between `Publisher` and `Address`,
we will pick `Publisher` as the owning side of the relationship.
The `Publisher` table will include a foreign key column
containing the id of the associated `Address` entity.

![one to one jpa](https://curriculum-content.s3.amazonaws.com/6036/java-mod-5-erd-to-jpa/one_to_one_jpa.png)

We use the `@OneToOne` JPA annotation to implement the
relationship between the `Publisher` and `Address` Java classes.

-  The `Publisher` class implements the owning side of the relationship
   by adding an instance variable to reference the associated `Address` object.
   Note the type of the variable is `Address`, not `int`, since we need
   to store a reference to an `Address` object not the foreign key integer value. 

   ```java
   @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
   private Address address;  //foreign key named address_id
   ```
   
-  The `Address` class implements the referencing or non-owning side
   of the relationship using the `mappedBy` attribute, which
   establishes the relationship as bidirectional.

   ```java
   @OneToOne(mappedBy = "address")
   private Publisher publisher;
   ```

Update `Publisher` and `Address` as shown to implement the relationship.
You will need to add getter/setter methods for the new fields as well:

```java
@Entity
public class Publisher {
   @Id
   @GeneratedValue
   private int id;

   @Column(unique = true, nullable = false)
   private String name;

   private String website;

   @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
   private Address address;

   //getters, setters, toString
}
```

```java
@Entity
public class Address {
   @Id
   @GeneratedValue
   private int id;

   private String street;

   @Column(nullable = false)
   private String city;

   private String state, zip;

   @Column(columnDefinition = "varchar(100) default 'United States'")
   private String country = "United States";

   @OneToOne(mappedBy = "address")
   private Publisher publisher;  //foreign key publisher_id

   //getters, setters, toString
}
```

The class on the owning side, i.e. `Publisher`,
should be used to create the relationship by
calling the `setAddress` method.

Edit `JpaCreate` to establish the one-to-one relationship
between each publisher and the headquarters address as shown below.
This should be done before persisting the objects.

```java
// Publisher - Owner one-to-one relationship
publisher1.setAddress(address1);
publisher2.setAddress(address2);
```

Now when we query the `Publisher` table, we see the `@OneToOne`
annotation established in the new foreign key column `address_id`.
The column name `address_id` is automatically derived from the field
name `address` and the primary key field named `id` of the `Address`
class. 


```sql
SELECT * 
FROM PUBLISHER;
```

| id  | name                  | website            | address_id  |
|-----|-----------------------|--------------------|-------------|
| 1   | HarperCollins         | harpercollins.com  | 3           |
| 2   | O'Reilly Media, Inc.  | oreilly.com        | 4           |


The name of the foreign key column can be altered
by adding a `@JoinColumn` annotation to the `address` field.  For example, 
we can set the name of the column to be `headquarters_id` rather
than the default `address_id` as shown:

```java
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name="headquarters_id")
    private Address address;
```

## Implementing The One-To-Many/Many-To-One Relationship with JPA

In a one-to-many relationship, the entity on the "many" side
is the owner of the relationship, since that is where the foreign
key referencing the entity on the "one" side should be stored.

`Book` is the owning side in the one-to-many relationship
between `Publisher` and `Book`, which can also be viewed as
a many-to-one relationship between `Book` and `Publisher`.
The `Book` table will store a foreign key `publisher_id`
referencing the associated `Publisher`.

![one to many jpa](https://curriculum-content.s3.amazonaws.com/6036/java-mod-5-erd-to-jpa/one_to_many_jpa.png)

-  The `Book` class implements the owning side of the relationship
   using a `@ManyToOne` annotation to reference the one associated `Publisher` object.     

   ```java
   @ManyToOne
   private Publisher publisher;   // foreign key publisher_id
   ```
   
-  The `Publisher` class implements the referencing or non-owning side
   of the relationship using the `@OneToMany` annotation and a collection
   such as a set or list of associated `Book` objects.
   The `@OneToMany` annotation includes the `mappedBy` attribute to establish
   the relationship as bidirectional.       

   ```java
   @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
   private Set<Book> books = new HashSet<>();
   ```

Update `Publisher` and `Address` as shown to implement the relationship.
You will need to add getter/setter methods for the new fields as well:

```java
@Entity
public class Book {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable=false)
    private String title;

    @Column(unique=true, nullable=false)
    private String isbn;

    @Column(name="publication_date")
    private LocalDate publicationDate;

    private int pages;

    @Column(columnDefinition = "integer default 1")
    private int edition = 1;

    @ManyToOne
    private Publisher publisher;

    //getters, setters, toString
}
```

```java
@Entity
public class Publisher {
   @Id
   @GeneratedValue
   private int id;

   @Column(unique = true, nullable = false)
   private String name;

   private String website;

   @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
   private Address address;

   @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL)
   private Set<Book> books = new HashSet<>();

   //getters, setters, toString
}
```

The class on the owning side, i.e. `Book`,
should be used to create the relationship by
calling the `setPublisher` method.

Edit `JpaCreate` to establish the many-to-one relationship
between each book and its publisher as shown below.
This should be done before persisting the objects.

```java
// Book > Publisher many-to-one relationship
book1.setPublisher(publisher1);
book2.setPublisher(publisher1);
book3.setPublisher(publisher2);
book4.setPublisher(publisher1);
```

Now when we query the `Book` table, we see the
relationship established in the new foreign key column `publisher_id`.

```sql
SELECT * 
FROM BOOK;
```

| id  | edition | isbn          | pages | publication_date | title                   | publisher_id   |
|-----|---------|---------------|-------|------------------|-------------------------|----------------|
| 9   | 1       | 9780063045415 | 40    | 2022-11-01       | The Sour Grape          | 1              |
| 10  | 1       | 9780060256654 | 64    | 2014-02-18       | The Giving Tree         | 1              |
| 11  | 2       | 9781492056355 | 790   | 2022-04-01       | Fluent Python           | 2              |
| 12  | 1       | 9780060256678 | 176   | 2014-02-18       | Where the Sidewalk Ends | 1              |


## Implementing The Many-To-Many Relationship with JPA

The many-to-many relationship between two entities is
not directly stored as a foreign key in either entity's table.  
As we've seen in a prior lesson, the many-to-many relationship requires
a new join table that has a composite key consisting
of the two entity's primary keys.

In terms of the JPA implementation, the many-to-many
relationship is stored in each entity class in a collection
such as a set.

We will still pick one of the entities as the "owning"
side of the relationship to establish the join table.
The non-owning side entity will be
defined using the `mappedBy` attribute.
We will create an association between two objects
by adding to the collection on the owning side
of the relationship.

Let's pick `Author` as the owning side in the many-to-many
relationship between `Author` and `Book`
. 

![many to many jpa](https://curriculum-content.s3.amazonaws.com/6036/java-mod-5-erd-to-jpa/many_to_many_jpa.png)

-  The `Author` class implements the owning side of the relationship
   using a `@ManyToMany` annotation to reference the collection of associated `Book` objects.
   The `@JoinTable` annotation is optional and can be used to specify the table and column names.     

   ```java
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Author_Book",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> books = new HashSet<>();
   ```
   
-  Instead of a setter, the `Author` class establishes the relationship with the method `addBook(Book book)`:   

   ```java
   public void addBook(Book book) {
        books.add(book);
   }
   ```
   
-  The `Book` class implements the referencing or non-owning side
   of the relationship using the `@ManyToMany` annotation to reference a list of associated `Author` objects.
   The `@ManyToMany` annotation includes the `mappedBy` attribute to establish
   the relationship as bidirectional.     

   ```java
   @ManyToMany(mappedBy="books")
   private Set<Author> authors = new HashSet<>();
   ```
   
-  If `Book` implements a method to add an author, the method should update the author's book list since
   `Author` is the owning side of the relationship.     

   ```java
   public void addAuthor(Author author) {
      authors.add(author);
      author.addBook(this); //update the owning side
   }
   ```

Update `Author` and `Book` as shown to implement the relationship.
You will need to add getter/adder methods for the new fields as well:

```java
package org.example.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Author {
   @Id
   @GeneratedValue
   private int id;

   @Column(name = "first_name")
   private String firstName;

   @Column(name = "last_name")
   private String lastName;

   @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(name = "Author_Book",
            joinColumns = @JoinColumn(name = "author_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
   private Set<Book> books = new HashSet<>();

   public void addBook(Book book) {
      books.add(book);
   }

   //getters, setters, toString
}
```


```java
@Entity
public class Book {
   @Id
   @GeneratedValue
   private int id;

   @Column(nullable=false)
   private String title;

   @Column(unique=true, nullable=false)
   private String isbn;

   @Column(name="publication_date")
   private LocalDate publicationDate;

   private int pages;

   @Column(columnDefinition = "integer default 1")
   private int edition = 1;

   @ManyToOne
   private Publisher publisher;

   @ManyToMany(mappedBy="books")
   private Set<Author> authors = new HashSet<>();

   public void addAuthor(Author author) {
      authors.add(author);
      author.addBook(this); //update the owning side
   }
   
   //getters, setters, toString
}
```


The class on the owning side, i.e. `Author`,
should be used to create the relationship by
calling the `addBook` method.

Edit `JpaCreate` to establish the many-to-many relationship
between books and authors as shown below.
This should be done before persisting the objects.

```java
// Author <> Book many-to-many relationship
author1.addBook(book1);
author2.addBook(book1);
author3.addBook(book2);
author3.addBook(book4);
author4.addBook(book3);
```

Now when we query the join table `Author_Book`, we see the
many-to-many relationship established by the composite primary key `(author_id, book_id)`.

```sql
SELECT * 
FROM Author_Book;
```

| author_id | book_id |
|-----------|---------|
| 5         | 9       |
| 6         | 9       |
| 7         | 10      |
| 7         | 12      |
| 8         | 11      |


## Retrieving the data from the database

1. Edit the `JpaRead` class to retrieve some of the entities we persisted
   in the database and verify the relationships:   
    ```java
    package org.example;
    
    import org.example.model.Address;
    import org.example.model.Author;
    import org.example.model.Book;
    import org.example.model.Publisher;
    
    import javax.persistence.EntityManager;
    import javax.persistence.EntityManagerFactory;
    import javax.persistence.Persistence;
    
    public class JpaRead {
        public static void main(String[] args) {
            // create EntityManager
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("example");
            EntityManager entityManager = entityManagerFactory.createEntityManager();
    
            // get publisher data
            Publisher publisher = entityManager.find(Publisher.class, 1);
            System.out.println(publisher);
            System.out.println(publisher.getAddress());
            System.out.println(publisher.getBooks());
    
            // get address data
            Address address = entityManager.find(Address.class, 3);
            System.out.println(address);
            System.out.println(address.getPublisher());
    
            // get author data
            Author author = entityManager.find(Author.class, 7);
            System.out.println(author);
            System.out.println(author.getBooks());
    
            // get book data
            Book book = entityManager.find(Book.class, 9);
            System.out.println(book);
            System.out.println(book.getPublisher());
            System.out.println(book.getAuthors());
    
            // close entity manager and factory
            entityManager.close();
            entityManagerFactory.close();
        }
    }
    ```
2. Update `persistence.xml` to set the `hibernate.hbm2ddl.auto` property to `none`.
   Recall that this prevents Hibernate from deleting the existing table data.    

   ```xml
   <property name="hibernate.hbm2ddl.auto" value="none" /> <!-- create / create-drop / update / none -->
   ```
   
3. Execute the `JpaRead.main` method to query the database tables using JPA.
4. The output from the print statements is interwoven with the Hibernate SQL commands.
   We can suppress the SQL output by editing `persistence.xml` and setting the `hibernate.show_sql` property to `false`.    

   ```xml
   <property name="hibernate.show_sql" value="false" /> <!-- Show SQL in console -->
   ```
   
5. Now when we execute the `JpaRead.main` method to query the database tables using JPA,
   the output is concise:

```text
Publisher{id=1, name='HarperCollins', website='harpercollins.com'}
Address{id=3, street='null', city='New York', state='NY', zip='null', country='United States'}
[Book{id=9, title='The Sour Grape', isbn='9780063045415', publicationDate=2022-11-01, pages=40, edition=0}, Book{id=12, title='Where the Sidewalk Ends', isbn='9780060256678', publicationDate=2014-02-18, pages=176, edition=1}, Book{id=10, title='The Giving Tree', isbn='9780060256654', publicationDate=2014-02-18, pages=64, edition=0}]
Address{id=3, street='null', city='New York', state='NY', zip='null', country='United States'}
Publisher{id=1, name='HarperCollins', website='harpercollins.com'}
Author{id=7, firstName='Shel', lastName='Silverstein'}
[Book{id=12, title='Where the Sidewalk Ends', isbn='9780060256678', publicationDate=2014-02-18, pages=176, edition=1}, Book{id=10, title='The Giving Tree', isbn='9780060256654', publicationDate=2014-02-18, pages=64, edition=0}]
Book{id=9, title='The Sour Grape', isbn='9780063045415', publicationDate=2022-11-01, pages=40, edition=0}
Publisher{id=1, name='HarperCollins', website='harpercollins.com'}
[Author{id=6, firstName='Pete', lastName='Oswald'}, Author{id=5, firstName='Jory', lastName='John'}]
```

## Conclusion

To implement a one-to-one relationship:

- One entity is chosen as the relationship owner.
- The table corresponding to the owning side will store the foreign key reference.
- The owning side Java class adds a new field with the annotation `@OneToOne`.
  - The field stores a single reference to the non-owning side entity.
- The non-owning side Java class adds a new field with the annotation `@OneToOne.`
   - The field stores a single reference to the owning side entity.
   - The `mappedBy` property references the `@OneToOne` field that was added to the owning side class.


To implement a one-to-many/many-to-one relationship:

- The entity on the "many" side of the relationship is the owner.
- The table corresponding to the owning side will store the foreign key reference.
- The owning side Java class adds a new field with the annotation `@ManyToOne`.
  - The field stores a single reference to non-owning side entity.
- The non-owning side Java class adds a new field with the annotation `@OneToMany`.
   - The field stores a collection of references to owning side entities.
   - The `mappedBy` property references the `@ManyToOne` field that was added to the owning side class.

To implement a many-to-many relationship:

- Pick one entity to be the owner of the relationship.
- A new join table is generated containing a composite primary key.
- The owning side Java class adds a new field with the annotation `@ManyToMany`.
   - The field stores a collection of references to non-owning side entities.
   - The optional `@JoinTable` annotation may be used to specify the join table name and the composite key column names.
   - Implement a method to add an entity to the collection.
- The non-owning side Java class adds a new field with the annotation `@ManyToMany`.
   - The field stores a collection of references to owning side entities.
   - The `mappedBy` property references the `@ManyToMany` field that was added to the owning side class.
   - If a method adds an entity to the non-owning side collection, the method should also add to the owning side collection.

You can [fork and clone](https://github.com/learn-co-curriculum/java-mod-5-erd-to-jpa) the final version of the project.