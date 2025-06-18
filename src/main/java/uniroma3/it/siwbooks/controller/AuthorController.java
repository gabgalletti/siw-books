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
import uniroma3.it.siwbooks.service.AuthorService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class AuthorController {
    @Autowired private AuthorService authorService;

    @GetMapping("/author/{id}")
    public String author(@PathVariable("id") Long id, Model model){
        Optional<Author> author = authorService.findById(id);
        if(author.isPresent())
            model.addAttribute("author", author.get());
        else
            model.addAttribute("author", new Author());
        return "author";
    }

    @GetMapping("/author/all")
    public String authorList(Model model) {
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
}
