package uniroma3.it.siwbooks.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.model.Review;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.repository.ReviewRepository;

import java.util.List;

@Service
public class ReviewService {
    @Autowired private ReviewRepository reviewRepository;

    public boolean existsByUserAndBook(User user, Book book){
        return findByUserAndBook(user, book) != null;
    }
    public Review findByUserAndBook(User user, Book book){
        return reviewRepository.findByUserAndBook(user, book);
    }

    public void save(Review newReview) {this.reviewRepository.save(newReview);}

    @Transactional
    public void deleteReviewsByBook(Book book) {
        List<Review> reviews = reviewRepository.findByBook(book);

        // Rimuovi il riferimento da user a review
        for(Review review : reviews) {
            User user = review.getUser();
            if(user != null && user.getReviews() != null) {
                user.getReviews().remove(review);
            }
        }

        // Ora puoi eliminare le recensioni
        reviewRepository.deleteAll(reviews);

    }
}
