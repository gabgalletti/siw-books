package uniroma3.it.siwbooks.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uniroma3.it.siwbooks.model.*;
import uniroma3.it.siwbooks.service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {
    @Autowired private BookService bookService;
    @Autowired private AuthorService authorService;
    @Autowired private ReviewService reviewService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private UserService userService;

    @GetMapping("/book/details/{id}")
    public String book(@PathVariable("id") Long id, Model model) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isEmpty()) {
            model.addAttribute("error", "Book not found");
            return "error";
        }
        Book book = bookOptional.get();
        List<Review> reviews = reviewService.findByBook(book);
        double averageRating = 0.0;
        if (!reviews.isEmpty()) {
            int sum = 0;
            for (Review r : reviews) {
                sum += r.getRating();
            }
            averageRating = (double) sum / reviews.size();
        }
        book.setAverageRating(averageRating);
        bookService.save(book);

        // Controllo delle credenziali dell'utente loggato
        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        if (loggedCredentials == null) {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("book", book);
            model.addAttribute("hasReviewed", false);
            model.addAttribute("favourited", false); // non essendo loggati, non esiste "preferito"
            model.addAttribute("userReview", null);
            model.addAttribute("reviews", book.getReviews());
            model.addAttribute("role", "NOROLE");
            return "book";
        }

        User userVerify = loggedCredentials.getUser();
        model.addAttribute("isLoggedIn", true);

        boolean hasReviewed = false;
        if (!userService.getLoggedUser().getReviews().isEmpty())
            hasReviewed = reviewService.existsByUserAndBook(userService.getLoggedUser(), book);

        List<Review> almostAllReviews = book.getReviews();
        if (hasReviewed) {

            Review userReview = reviewService.findByUserAndBook(userService.getLoggedUser(), book);
            model.addAttribute("userReview", userReview);
            almostAllReviews.remove(userReview);
            model.addAttribute("reviews", almostAllReviews);
        } else {
            model.addAttribute("userReview", null);
            model.addAttribute("reviews", almostAllReviews);
        }
        // Controllo se è uno dei libri preferiti
        model.addAttribute("role", loggedCredentials.getRole());
        boolean favourited = userService.getLoggedUser().getFavouriteBooks().contains(book);
        model.addAttribute("favourited", favourited);


        model.addAttribute("averageRating", book.getAverageRating());
        model.addAttribute("book", book);
        model.addAttribute("hasReviewed", hasReviewed);

        return "book";
    }
    @PostMapping("/book/details/{id}")
    public String addToFavourites(@PathVariable("id") Long id,
                                  @ModelAttribute("favourited") boolean favourited,
                                  Model model) {
        Book book = bookService.findById(id).get();
        favourited = !favourited; // inversione del valore
        if (favourited) {
            userService.getLoggedUser().getFavouriteBooks().add(book);
        } else {
            userService.getLoggedUser().getFavouriteBooks().remove(book);
        }




        userService.save(userService.getLoggedUser());


        model.addAttribute("book", book);
        model.addAttribute("favourited", userService.getLoggedUser().getFavouriteBooks().contains(book));
        return "redirect:/book/details/" + id;

    }
    @GetMapping("/book/edit/{id}")
    public String showEditBookForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id).get();
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        return "formAddBook";
    }
    @PostMapping("/book/edit/{id}")
    public String updateBook(@PathVariable Long id,
                             @ModelAttribute("book") Book updatedBook,
                             @RequestParam(value = "authors[]", required = false) List<Long> authorsIds,
                             RedirectAttributes redirectAttributes) {
        Optional<Book> optionalBook = bookService.findById(id);
        if (optionalBook.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Libro non trovato!");
            return "redirect:/book/all";
        }

        Book existingBook = optionalBook.get();


        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setYear(updatedBook.getYear());
        existingBook.setDescription(updatedBook.getDescription());

        // aggiornamento autori associati
        if (authorsIds != null && !authorsIds.isEmpty()) {
            for(Author a: authorService.findByIds(authorsIds)) {
                a.getBooks().add(existingBook);
                existingBook.getAuthors().add(a);
            }
        } else {
            existingBook.setAuthors(new ArrayList<>());
        }


        bookService.save(existingBook);



        return "redirect:/book/all";
    }


    @PostMapping("/book/saveReview/{id}")
    public String saveReview(@PathVariable("id") Long id,
                            @RequestParam("rating") int rating,
                            @RequestParam("title") String title,
                            @RequestParam("reviewDescription") String reviewDescription, Model model) {      //problema con id di dummyReview: viene passato un id = 1


        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isEmpty()) {
            model.addAttribute("error", "Book not found");
            return "error";
        }
        Book book = bookOptional.get();

        User loggedUser = userService.getLoggedUser();
        if (loggedUser == null) {
            model.addAttribute("error", "User not found");
            return "error";
        }

        // Qui il controllo che hai già:
        if (reviewService.existsByUserAndBook(loggedUser, book)) {
            model.addAttribute("error", "You have already reviewed this book");
            return "redirect:/book/details/" + id + "?error=alreadyReviewed";
        }

        // Ora crei esplicitamente una NUOVA istanza di Review
        Review newReview = new Review();
        newReview.setTitle(title);
        newReview.setRating(rating);
        newReview.setReviewDescription(reviewDescription);
        newReview.setBook(book);
        newReview.setUser(loggedUser);

        // Salvi la nuova recensione
        reviewService.save(newReview);


        bookService.save(book);
        return "redirect:/book/details/" + id;

    }

    @GetMapping("/book/all")
    public String bookList(Model model){
        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        if(loggedCredentials == null){
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("role", "NOROLE");
        } else {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("role", loggedCredentials.getRole());
        }

        model.addAttribute("books", bookService.findAll());
        return "bookList";
    }


    @GetMapping("/book/new")
    public String bookNew(Model model){
        List<Author> authors = new ArrayList<>();
        authorService.findAll().forEach(authors::add);
        model.addAttribute("authors", authors);
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "formAddBook";
    }

    @PostMapping("/book/new")
    public String bookNew(Book book,
                          @RequestParam(value = "authors[]", required = false) List<Long> authorsIds,
                          @ModelAttribute("image")MultipartFile image,
                          Model model) {
        List<Author> authors = authorService.findByIds(authorsIds);
        book.setAuthors(authors);
        String fileName = book.getTitle() + book.getAuthors().get(0).getName() + '.' + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.') + 1);
        try{
            Path uploadPath = Paths.get("C:/Users/Gabriele/Desktop/uploads-siw-books/books-cover/");
            if (!Files.exists(uploadPath)) {
               Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            image.transferTo(filePath.toFile());
            book.setImageUrl(String.format("/books-cover/%s", fileName));
            for(Author a: book.getAuthors())
                a.getBooks().add(book);
            bookService.save(book);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/book/all";
    }

    @GetMapping("/home")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser");

        model.addAttribute("isLoggedIn", isLoggedIn);


        model.addAttribute("mostLikedBooks", bookService.findMostLiked());
        model.addAttribute("newBooks", bookService.findLatestBooks());
        model.addAttribute("topAuthors", authorService.findTopAuthors());
        return "home";
    }

    @GetMapping("/book/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id, Model model) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isEmpty()) {
            model.addAttribute("error", "Book not found");
            return "error";
        }
        Book book = bookOptional.get();

        bookService.delete(book);
        return "redirect:/book/all";
    }
    @GetMapping("review/edit/{id}")
    public String editReview(@PathVariable("id") Long id, Model model) {
        Optional<Review> reviewOptional = reviewService.findById(id);
        if (reviewOptional.isEmpty()) {
            model.addAttribute("error", "Review not found");
            return "error";
        }
        Review review = reviewOptional.get();
        model.addAttribute("review", review);
        return "formEditReview";
    }
    @PostMapping("review/edit/{id}")
    public String editReview(@PathVariable("id") Long id,
                             @RequestParam("rating") int rating,
                             @RequestParam("title") String title,
                             @RequestParam("reviewDescription") String reviewDescription, Model model) {
        Optional<Review> reviewOptional = reviewService.findById(id);
        if (reviewOptional.isEmpty()) {
            model.addAttribute("error", "Review not found");
            return "error";
        }
        Review review = reviewOptional.get();
        if(credentialsService.getLoggedCredentials().getUser().getId().equals(review.getUser().getId())){
            review.setTitle(title);
            review.setRating(rating);
            review.setReviewDescription(reviewDescription);
            review.setBook(review.getBook());
            review.setUser(review.getUser());
            reviewService.save(review);
            bookService.save(review.getBook());
            List<Review> reviews = reviewService.findAll();
        } else {
            model.addAttribute("error", "You can't edit this review");
            return "error";
        }
        return "redirect:/book/details/" + review.getBook().getId();
    }
    @GetMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable("id") Long id, Model model) {
        Optional<Review> reviewOptional = reviewService.findById(id);
        if (reviewOptional.isEmpty()) {
            model.addAttribute("error", "Review not found");
            return "error";
        }
        Review review = reviewOptional.get();

        Credentials credentials = credentialsService.getLoggedCredentials();
        if (credentials == null) {
            model.addAttribute("error", "User not found");
        }
        User user = credentials.getUser();
        if (!user.getReviews().contains(review) && !credentials.getRole().equals("ADMIN")) {
            model.addAttribute("error", "You can't delete this review");
            return "error";
        } else if(user.getReviews().contains(review)){
            user.getReviews().remove(review);
        } else if(credentials.getRole().equals("ADMIN")){
            User userTemp = review.getUser();
            userTemp.getReviews().remove(review);
        }
        List<Review> reviews = reviewService.findAll();
        reviews.remove(review);
        reviewService.delete(review);


        return "redirect:/book/details/" + review.getBook().getId();
    }
}
