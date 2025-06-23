package uniroma3.it.siwbooks.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.model.Review;
import uniroma3.it.siwbooks.model.User;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.user = :user AND r.book = :book")
    List<Review> findByUserAndBook(@Param("user") User user, @Param("book") Book book);

    List<Review> findByUser(User user);
    void deleteByBook(Book book);
    List<Review> findByBook(Book book);
}
