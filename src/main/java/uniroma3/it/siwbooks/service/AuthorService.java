package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Author;
import uniroma3.it.siwbooks.repository.AuthorRepository;

import java.util.Optional;

@Service
public class AuthorService {
    @Autowired private AuthorRepository authorRepository;

    public Optional<Author> findById(Long id) {return this.authorRepository.findById(id);}


    public Iterable<Author> findAll() {return this.authorRepository.findAll();}

    public void save(Author author) {authorRepository.save(author);}
}
