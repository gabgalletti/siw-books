package uniroma3.it.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.model.Review;
import uniroma3.it.siwbooks.model.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Review findByUserAndBook(User user, Book book);
}
