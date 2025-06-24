package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Author;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.repository.AuthorRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    @Autowired private AuthorRepository authorRepository;
    @Autowired
    private BookService bookService;

    public Optional<Author> findById(Long id) {return this.authorRepository.findById(id);}


    public Iterable<Author> findAll() {return this.authorRepository.findAll();}

    public void save(Author author) {authorRepository.save(author);}

    public List<Author> findTopAuthors() {

        List<Author> authors = this.authorRepository.findAll();


        return authors.stream()
                .sorted(Comparator
                        .comparing(AuthorService::getTotalFavorites).reversed()
                        .thenComparing(AuthorService::getAverageRating).reversed()
                )
                .limit(5)
                .collect(Collectors.toList());
    }


    private static int getTotalFavorites(Author author) {
        if (author.getBooks() == null) return 0;
        return author.getBooks().stream()
                .filter(book -> book.getUsers() != null)
                .mapToInt(book -> book.getUsers().size())
                .sum(); // Somma il numero di favoriti per ogni libro
    }


    private static double getAverageRating(Author author) {
        if (author.getBooks() == null) return 0.0;
        return author.getBooks().stream()
                .filter(book -> book.getAverageRating() != 0)
                .mapToDouble(Book::getAverageRating)
                .average()
                .orElse(0.0);
    }

    public List<Author> searchAuthorsByName(String keyword) {
        List<Author> allAuthors = this.authorRepository.findAll();

        if (keyword == null || keyword.isEmpty()) {
            return allAuthors;
        }

        String lowerKeyword = keyword.toLowerCase();
        return allAuthors.stream()
                .filter(author -> author.getName().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }
    public void removeAuthorFromBook(Author author, Book book) {
        book.getAuthors().remove(author);
        bookService.save(book);
    }

    public void delete(Author author) {
        List<Book> booksToDelete = new ArrayList<>(author.getBooks());
        for(Book b: booksToDelete)
            removeAuthorFromBook(author, b);

        this.authorRepository.delete(author);
    }

    public List<Author> findByIds(List<Long> authorsIds) {return this.authorRepository.findAllById(authorsIds);}
}
