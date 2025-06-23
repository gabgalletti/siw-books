package uniroma3.it.siwbooks.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.model.Review;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.repository.BookRepository;
import uniroma3.it.siwbooks.repository.ReviewRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class BookService{
    @Autowired private BookRepository bookRepository;
    @Autowired
    private ReviewService reviewService;


    @Transactional
    public Iterable<Book> findAll(){return this.bookRepository.findAll();}

    @Transactional
    public Optional<Book> findById(Long id) {return this.bookRepository.findById(id);}

    @Transactional
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

    public List<Book> findLatestBooks() {
        List<Book> booksList = new ArrayList<>();
        this.bookRepository.findAll().forEach(booksList::add);
        booksList.sort(Comparator.comparing(Book::getCreatedAt).reversed()); // Ordina per data decrescente
        return booksList.size() > 4 ? booksList.subList(0, 4) : booksList; // Ritorna massimo 4 libri
    }

    @Transactional
    public List<Book> searchBooksByTitle(String keyword) {
        List<Book> allBooks = new ArrayList<>();
        this.bookRepository.findAll().forEach(allBooks::add);

        if (keyword == null || keyword.isEmpty()) {
            return allBooks;
        }

        String lowerKeyword = keyword.toLowerCase();
        return allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }


    public void delete(Book book) {
        for(User u: book.getUsers())
            u.getFavouriteBooks().remove(book);

        reviewService.deleteReviewsByBook(book);
        this.bookRepository.delete(book);
    }

}
