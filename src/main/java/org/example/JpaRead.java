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
