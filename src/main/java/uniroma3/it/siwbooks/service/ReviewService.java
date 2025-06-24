package uniroma3.it.siwbooks.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.model.Review;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.repository.BookRepository;
import uniroma3.it.siwbooks.repository.ReviewRepository;
import uniroma3.it.siwbooks.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookRepository bookRepository;

    public List<Review> findAll(){return reviewRepository.findAll();}

    public boolean existsByUserAndBook(User user, Book book){
        return findByUserAndBook(user, book) != null;
    }
    public Review findByUserAndBook(User user, Book book) {
        List<Review> reviews = reviewRepository.findByUserAndBook(user, book);
        if (!reviews.isEmpty()) {
            return reviews.get(0);
        }
        return null;
    }


    @Transactional
    public void save(Review review) {
        //Assicurati che user e book siano caricati dal db e quindi managed
        User managedUser = userRepository.findById(review.getUser().getId()).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Book managedBook = bookRepository.findById(review.getBook().getId()).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        review.setUser(managedUser);
        review.setBook(managedBook);
        reviewRepository.save(review);
    }

    public Optional<Review> findById(Long id) {return reviewRepository.findById(id);}

    public void delete(Review review) {reviewRepository.delete(review);}


    @Transactional
    public void deleteReviewsByBook(Book book) {
        this.reviewRepository.deleteByBook(book);
    }

    public List<Review> findByBook(Book book) {
        return this.reviewRepository.findByBook(book);
    }
}
