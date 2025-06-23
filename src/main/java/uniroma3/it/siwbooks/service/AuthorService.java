package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Author;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.repository.AuthorRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {
    @Autowired private AuthorRepository authorRepository;

    public Optional<Author> findById(Long id) {return this.authorRepository.findById(id);}


    public Iterable<Author> findAll() {return this.authorRepository.findAll();}

    public void save(Author author) {authorRepository.save(author);}

    public List<Author> findTopAuthors() {
        // Ottieni tutti gli autori
        List<Author> authors = this.authorRepository.findAll();

        // Ordina per numero di libri nei preferiti (decrescente), poi per media dei rating (secondario)
        return authors.stream()
                .sorted(Comparator
                        .comparing(AuthorService::getTotalFavorites).reversed() // Ordina per numero totale di favoriti
                        .thenComparing(AuthorService::getAverageRating).reversed() // Secondariamente per media dei rating
                )
                .limit(5) // Mostra solo i primi 5 autori
                .collect(Collectors.toList());
    }

    // Metodo per calcolare il numero totale di libri nei preferiti
    private static int getTotalFavorites(Author author) {
        if (author.getBooks() == null) return 0;
        return author.getBooks().stream()
                .filter(book -> book.getUsers() != null)
                .mapToInt(book -> book.getUsers().size())
                .sum(); // Somma il numero di favoriti per ogni libro
    }

    // Metodo per calcolare la media del rating di tutti i libri dell'autore
    private static double getAverageRating(Author author) {
        if (author.getBooks() == null) return 0.0;
        return author.getBooks().stream()
                .filter(book -> book.getAverageRating() != 0) // Filtra solo i libri con rating valido
                .mapToDouble(Book::getAverageRating) // Recupera i rating dei libri
                .average()
                .orElse(0.0); // Media dei rating o 0.0 se non ci sono valutazioni
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

    public void delete(Author author) {
        this.authorRepository.delete(author);
    }

}
