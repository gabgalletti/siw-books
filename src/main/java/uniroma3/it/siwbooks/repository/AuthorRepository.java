package uniroma3.it.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uniroma3.it.siwbooks.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
