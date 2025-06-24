package uniroma3.it.siwbooks.controller;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import uniroma3.it.siwbooks.model.Author;
import uniroma3.it.siwbooks.model.Book;
import uniroma3.it.siwbooks.model.Credentials;
import uniroma3.it.siwbooks.service.AuthorService;
import uniroma3.it.siwbooks.service.CredentialsService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class AuthorController {
    @Autowired private AuthorService authorService;
    @Autowired private CredentialsService credentialsService;


    @GetMapping("/author/details/{id}")
    public String author(@PathVariable("id") Long id, Model model){
        Optional<Author> author = authorService.findById(id);
        if(author.isPresent())
            model.addAttribute("author", author.get());
        else
            model.addAttribute("author", new Author());

        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        if(loggedCredentials == null){
            model.addAttribute("isLoggedIn", false);
        } else {
            model.addAttribute("isLoggedIn", true);
        }
        return "author";
    }

    @GetMapping("/author/all")
    public String authorList(Model model) {
        Credentials credentials = credentialsService.getLoggedCredentials();
        if (credentials == null) {
            model.addAttribute("isLoggedIn", false);
            model.addAttribute("role", "NOROLE");
        } else{
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("role", credentials.getRole());
        }
        model.addAttribute("authors", authorService.findAll());
        return "authorList";
    }

    @GetMapping("/author/new")
    public String authorNew(Model model) {
        model.addAttribute("author", new Author());
        return "formAddAuthor";
    }

    @PostMapping("/author/new")
    public String authorNew(Author author,
                            @ModelAttribute("image") MultipartFile image,
                            Model model) {
        String fileName = author.getName() + author.getId() + '.' + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.') + 1);
        try{
            Path uploadPath = Paths.get("C:/Users/Gabriele/Desktop/uploads-siw-books/author-photo/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            image.transferTo(filePath.toFile());
            author.setImageUrl(String.format("/author-photo/%s", fileName));
            authorService.save(author);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/author/all";
    }

    @GetMapping("/author/edit/{id}")
    public String showEditAuthorForm(@PathVariable Long id, Model model) {
        Author author = authorService.findById(id).get();
        model.addAttribute("author", author);
        return "formAddAuthor";
    }
    @PostMapping("/author/edit/{id}")
    public String updateAuthor(@PathVariable Long id,
                               @ModelAttribute("author") Author updatedAuthor,
                               Model model){
        Optional<Author> authorOptional = authorService.findById(id);
        if (authorOptional.isEmpty()) {
            model.addAttribute("error", "Author not found");
            return "error";
        }
        Author author = authorOptional.get();
        author.setName(updatedAuthor.getName());
        author.setSurname(updatedAuthor.getSurname());
        author.setYearOfBirth(updatedAuthor.getYearOfBirth());
        author.setYearOfDeath(updatedAuthor.getYearOfDeath());
        author.setNationality(updatedAuthor.getNationality());
        author.setDescription(updatedAuthor.getDescription());
        authorService.save(author);
        return "redirect:/author/all";
    }
    @GetMapping("/author/delete/{id}")
    public String deleteAuthor(@PathVariable("id") Long id, Model model) {
        Optional<Author> authorOptional = authorService.findById(id);
        if (authorOptional.isEmpty()) {
            model.addAttribute("error", "Author not found");
            return "error";
        }
        Author author = authorOptional.get();
        authorService.delete(author);
        return "redirect:/author/all";
    }
}
