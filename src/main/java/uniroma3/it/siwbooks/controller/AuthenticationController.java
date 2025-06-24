package uniroma3.it.siwbooks.controller;

import uniroma3.it.siwbooks.model.Credentials;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.service.CredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {
    @Autowired private CredentialsService credentialsService;
    @Autowired private PasswordEncoder passwordEncoder;
    @GetMapping("/login")
    public String showLoginForm() {
        return "loginForm";
    }

    @PostMapping("/login")
    public String login() {
        return "redirect:/success";
    }


    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("credentials", new Credentials());
        return "registerForm";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, @ModelAttribute Credentials credentials) {
        credentials.setUser(user);
        credentials.setRole(Credentials.DEFAULT_ROLE); // default role
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        credentialsService.save(credentials);
        return "redirect:/login";
    }

    @GetMapping("/success")
    public String successRedirect() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "redirect:/book/all";
    }
}


