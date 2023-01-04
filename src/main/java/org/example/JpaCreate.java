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
        address1.setCountry("United States");

        Address address2 = new Address();
        address2.setCity("Sebastopol");
        address2.setState("CA");
        address2.setCountry("United States");

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

        // create publisher-address relationship
        publisher1.setAddress(address1);
        publisher2.setAddress(address2);

        // create publisher < book relationship
        book1.setPublisher(publisher1);
        book2.setPublisher(publisher1);
        book3.setPublisher(publisher2);
        book4.setPublisher(publisher1);

        // create book <> author relationship
        author1.addBook(book1);
        author2.addBook(book1);
        author3.addBook(book2);
        author3.addBook(book4);
        author4.addBook(book3);

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
