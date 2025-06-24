package uniroma3.it.siwbooks.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String surname;
    private int yearOfBirth;
    private int yearOfDeath;
    private String nationality;
    @Lob
    private String description;
    private String imageUrl;

    @ManyToMany(mappedBy = "authors", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Book> books;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname(){return this.surname;}

    public void setSurname(String surname){this.surname = surname;}

    public int getYearOfBirth() {return yearOfBirth;}

    public void setYearOfBirth(int yearOfBirth) {this.yearOfBirth = yearOfBirth;}

    public int getYearOfDeath(){return this.yearOfDeath;}

    public void setYearOfDeath(int yearOfDeath){this.yearOfDeath = yearOfDeath;}

    public String getNationality() {return nationality;}

    public void setNationality(String nationality) {this.nationality = nationality;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }


}
