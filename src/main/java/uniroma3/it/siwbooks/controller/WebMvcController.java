package uniroma3.it.siwbooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Controller
public class WebMvcController implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/books-cover/**")
                .addResourceLocations("file:C:/Users/Gabriele/Desktop/uploads-siw-books/books-cover/");
        registry.addResourceHandler("/author-photo/**")
                .addResourceLocations("file:C:/Users/Gabriele/Desktop/uploads-siw-books/author-photo/");
    }
}
