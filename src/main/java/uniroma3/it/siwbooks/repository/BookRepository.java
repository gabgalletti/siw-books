package uniroma3.it.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import uniroma3.it.siwbooks.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
