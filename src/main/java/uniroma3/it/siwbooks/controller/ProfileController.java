package uniroma3.it.siwbooks.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import uniroma3.it.siwbooks.model.Credentials;
import uniroma3.it.siwbooks.model.User;
import uniroma3.it.siwbooks.service.CredentialsService;
import uniroma3.it.siwbooks.service.UserService;
@Controller
public class ProfileController {

    @Autowired private UserService userService;
    @Autowired
    private CredentialsService credentialsService;
    @Autowired private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        return "profile";
    }
    @GetMapping("/profile/account")
    public String account(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("credentials", credentialsService.getLoggedCredentials());
        return "account";
    }
    @GetMapping("/profile/account/edit")
    public String updateAccount(Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("credentials", credentialsService.getLoggedCredentials());
        return "formEditAccount";
    }
    @PostMapping("/profile/account/edit")
    public String updateAccount(@ModelAttribute User updatedUserData, @ModelAttribute Credentials updatedCredentials) {
        Credentials loggedCredentials = credentialsService.getLoggedCredentials();
        User existingUser = loggedCredentials.getUser();

        // Aggiorna solo i campi necessari dell'utente esistente
        existingUser.setName(updatedUserData.getName());
        existingUser.setSurname(updatedUserData.getSurname());
        existingUser.setEmail(updatedUserData.getEmail());
        // Aggiungi qui eventuali altri campi che desideri aggiornare

        // Aggiorna i campi delle credenziali
        loggedCredentials.setUsername(updatedCredentials.getUsername());
        if (updatedCredentials.getPassword() != null && !updatedCredentials.getPassword().isEmpty()) {
            loggedCredentials.setPassword(passwordEncoder.encode(updatedCredentials.getPassword()));
        }

        // Salva le credenziali aggiornate con l'utente aggiornato
        credentialsService.save(loggedCredentials);

        return "redirect:/profile";


    }

}
