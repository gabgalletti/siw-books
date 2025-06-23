package uniroma3.it.siwbooks.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.model.Review;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {
    @Autowired private BookService bookService;
    @Autowired private AuthorService authorService;
    @Autowired private ReviewService reviewService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private UserService userService;

    @GetMapping("/book/{id}")
    public String book(@PathVariable("id") Long id,Model model){
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isEmpty()) {
            model.addAttribute("error", "Book not found");
            return "error"; // Pagina di errore (deve essere ancora implementata)
        }
        Book book = bookOptional.get();


        model.addAttribute("averageRating", book.getAverageRating());



        //ricavo utente loggato
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User userVerify = credentialsService.findByUsername(username).getUser();
        if(userVerify == null){
            model.addAttribute("error", "User not found");
            return "error"; // Pagina di errore (deve essere ancora implementata)
        }

        boolean hasReviewed = false;
        if(!userService.getLoggedUser().getReviews().isEmpty())
            hasReviewed = reviewService.existsByUserAndBook(userService.getLoggedUser(), book);

        if (hasReviewed) {
            // Se esiste la recensione, aggiungila al modello
        } else {
            // Se non esiste la recensione, prepara il form

        }

        //Controllo se è uno dei libri preferiti
        boolean favourited = false;
        if(userService.getLoggedUser().getFavouriteBooks().contains(book))
            favourited = true;
        model.addAttribute("favourited", favourited);

        model.addAttribute("book", book);
        model.addAttribute("hasReviewed", hasReviewed);

        return "book";
    }
    @PostMapping("/book/{id}")
    public String addToFavourites(@PathVariable("id") Long id,
                                  @ModelAttribute("favourited") boolean favourited,
                                  Model model) {
        Book book = bookService.findById(id).get();
        favourited = !favourited; // Inverti lo stato
        if (favourited) {
            userService.getLoggedUser().getFavouriteBooks().add(book);
        } else {
            userService.getLoggedUser().getFavouriteBooks().remove(book);
        }

        // Salva i cambiamenti
        userService.save(userService.getLoggedUser());

        // Aggiorna il modello con il nuovo stato
        model.addAttribute("book", book);
        model.addAttribute("favourited", userService.getLoggedUser().getFavouriteBooks().contains(book)); // Inverti lo stato
        return "redirect:/book/" + id; // Torna alla stessa pagina"; // Torna alla stessa pagina

    }

    @PostMapping("/book/{id}/saveReview")
    public String saveReview(@PathVariable("id") Long id,
                            @RequestParam("rating") int rating,
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
            return "redirect:/book/" + id + "?error=alreadyReviewed"; // O la gestione errori che preferisci
        }

        // Ora crei esplicitamente una NUOVA istanza di Review
        Review newReview = new Review();
        newReview.setRating(rating);
        newReview.setReviewDescription(reviewDescription);
        newReview.setBook(book);
        newReview.setUser(loggedUser);

        // Salvi la nuova recensione
        reviewService.save(newReview);

        List<Review> reviews = reviewService.findByBook(book);
        double averageRating = 0.0;
        if (!reviews.isEmpty()) { // Controllo per evitare divisione per zero
            int sum = 0;
            for (Review r : reviews) {
                sum += r.getRating();
            }
            averageRating = (double) sum / reviews.size();
        }
        book.setAverageRating(averageRating);
        bookService.save(book);
        return "redirect:/book/" + id;

    }

    @GetMapping("/book/all")
    public String bookList(Model model){
        model.addAttribute("credentials", credentialsService.getLoggedCredentials());
        model.addAttribute("books", bookService.findAll());
        return "bookList";
    }


    @GetMapping("/book/new")
    public String bookNew(Model model){
        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.findAll());
        return "formAddBook";
    }

    @PostMapping("/book/new")
    public String bookNew(Book book,
                          @ModelAttribute("image")MultipartFile image,
                          Model model) {

        String fileName = book.getTitle() + book.getAuthor().getName() + '.' + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.') + 1);
        try{
            Path uploadPath = Paths.get("C:/Users/Gabriele/Desktop/uploads-siw-books/books-cover/");
            if (!Files.exists(uploadPath)) {
               Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            image.transferTo(filePath.toFile());
            book.setImageUrl(String.format("/books-cover/%s", fileName));
            book.getAuthor().getBooks().add(book);
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
}
