package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.repository.BookRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BookService{
    @Autowired private BookRepository bookRepository;

    public Iterable<Book> findAll(){return this.bookRepository.findAll();}

    public Optional<Book> findById(Long id) {return this.bookRepository.findById(id);}

    public void save(Book book) {this.bookRepository.save(book);}
}
