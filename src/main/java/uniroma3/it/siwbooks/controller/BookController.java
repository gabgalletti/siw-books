package uniroma3.it.siwbooks.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    User user;

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
        this.user = userVerify;
        boolean hasReviewed = false;
        if(!this.user.getReviews().isEmpty())
            hasReviewed = reviewService.existsByUserAndBook(this.user, book);

        if (hasReviewed) {
            // Se esiste la recensione, aggiungila al modello
            Review review = reviewService.findByUserAndBook(this.user, book);
            model.addAttribute("review", review);
        } else {
            // Se non esiste la recensione, prepara il form
            model.addAttribute("newReview", new Review());
        }

        //Controllo se è uno dei libri preferiti
        boolean favourited = false;
        if(this.user.getFavouriteBooks().contains(book))
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
            this.user.getFavouriteBooks().add(book);
        } else {
            this.user.getFavouriteBooks().remove(book);
        }

        // Salva i cambiamenti
        userService.save(user);

        // Aggiorna il modello con il nuovo stato
        model.addAttribute("book", book);
        model.addAttribute("favourited", user.getFavouriteBooks().contains(book)); // Inverti lo stato
        return "redirect:/book/" + id; // Torna alla stessa pagina"; // Torna alla stessa pagina

    }

    @PostMapping("/book/{id}/saveReview")
    public String saveReview(@PathVariable("id") Long id, Review newReview, Model model) {
        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isEmpty()) {
            model.addAttribute("error", "Book not found");
            return "error";
        }
        Book book = bookOptional.get();

        // Assegna il libro e l'utente alla nuova recensione
        newReview.setBook(book);
        newReview.setUser(this.user);

        // Controlla se la recensione esiste (facoltativo, per evitare duplicati lato applicazione)
        if (reviewService.existsByUserAndBook(this.user, book)) {
            model.addAttribute("error", "You have already reviewed this book");
            return "error";
        }
        book.getReviews().add(newReview);
        // Salva la recensione
        reviewService.save(newReview);

        List<Review> reviews = book.getReviews();
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
        model.addAttribute("mostLikedBooks", bookService.findMostLiked());
        return "home";
    }
}
