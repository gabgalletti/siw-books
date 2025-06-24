package uniroma3.it.siwbooks.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .authoritiesByUsernameQuery("SELECT username, user_role FROM credentials WHERE username=?")
                .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable().cors().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/login", "/books-cover/**", "/author-photo/**", "/css/**", "/js/**", "/images/**", "/favicon.ico","/logout", "/home", "/book/all", "/author/all", "/book/details/**", "/author/details/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/register", "/login", "/logout", "home", "/author/details/**", "/author/all", "/book/all").permitAll()
                .requestMatchers(HttpMethod.GET, "/book/saveReview/**", "/profile/**", "/review/**").hasAnyAuthority("DEFAULT", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/book/saveReview/**", "/profile/**", "/book/details/**", "/review/**").hasAnyAuthority("DEFAULT", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/book/new", "/author/new", "/book/delete/**", "/author/delete/**", "/book/edit/**", "/author/edit/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/book/new", "/author/new", "/book/delete/**", "/author/delete/**", "/book/edit/**", "/author/edit/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and().formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/success", true)
                .failureUrl("/login?error=true")
                .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();

        return http.build();
    }
}
