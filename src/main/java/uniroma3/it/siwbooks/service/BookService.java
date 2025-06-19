package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.repository.BookRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class BookService{
    @Autowired private BookRepository bookRepository;

    public Iterable<Book> findAll(){return this.bookRepository.findAll();}

    public Optional<Book> findById(Long id) {return this.bookRepository.findById(id);}

    public void save(Book book) {this.bookRepository.save(book);}

    public List<Book> findMostLiked() {
        Iterable<Book> allBooks = this.findAll(); // Recupera tutti i libri
        List<Book> booksList = new ArrayList<>();
        allBooks.forEach(booksList::add); // Converte Iterable in una lista

        // Ordina i libri secondo averageRating in ordine decrescente,
        // e in caso di parità, secondo il numero di utenti che li preferiscono
        booksList.sort(Comparator
                .comparingDouble(Book::getAverageRating).reversed() // Ordina per averageRating decrescente
                .thenComparing(book -> book.getUsers() != null ? -book.getUsers().size() : 0) // Se stessi rating, ordina per numero di utenti decrescente
        );

        return booksList.size() > 4 ? booksList.subList(0, 4) : booksList;


    }
}
