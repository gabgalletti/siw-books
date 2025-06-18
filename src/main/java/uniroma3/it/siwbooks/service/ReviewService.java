package uniroma3.it.siwbooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.model.Review;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.repository.ReviewRepository;

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
}
