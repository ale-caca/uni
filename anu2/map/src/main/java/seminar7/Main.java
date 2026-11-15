package seminar7;

import seminar7.domain.Movie;
import seminar7.domain.validators.MovieValidator;
import seminar7.domain.validators.ValidationException;
import seminar7.domain.validators.Validator;
import seminar7.repository.Repository;
import seminar7.repository.RepositoryException;
import seminar7.repository.db.MovieDBRepository;
import seminar7.repository.factory.RepositoryFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.*;
import java.util.stream.Collectors;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        try {
            Repository<Long, Movie> repo = createRepository();
           //TODO :Uncomment one by one, while you implement the requirements
            validation();
           // readAll(repo);
           // readOne(repo);
           // create(repo);
           // update(repo);
           // delete(repo);
           // factoryFile();
            //searchByDirector(repo);
            //reports(repo);
        } catch (RepositoryException e) {
            System.out.println("Error at db: " + e.getMessage());
        }
    }

    static void validation() {
        System.out.println("=== Validation ===");
        MovieValidator validator = new MovieValidator();

        validator.validate(new Movie("Barbie", "Greta Gerwig", 2023, 114));
        System.out.println("a) Movie Barbie is valid");

        try {
            validator.validate(new Movie("", "Unknown", 1800, 0));
        } catch (ValidationException e) {
            System.out.println("b) Movie is not valid: " + e.getMessage());
        }
    }

    static void readAll(Repository<Long, Movie> repo) {
        System.out.println("=== Read all ===");
        repo.findAll().forEach(System.out::println);
    }

    static void readOne(Repository<Long, Movie> repo) {
        System.out.println("=== Read one ===");
        Long first = repo.stream().findFirst().get().getId();

        Optional<Movie> found = repo.findOne(first);
        System.out.println("a) " + (found.isPresent() ? "Found movie: " + found.get() : "Movie does not exist"));

        Optional<Movie> notFound = repo.findOne(9999L);
        System.out.println("b) " + (notFound.isPresent() ? "Found movie: " + notFound.get() : "Movie does not exist (id 9999)"));

        try {
            repo.findOne(null);
        } catch (IllegalArgumentException e) {
            System.out.println("c) The searched id cannot be null");
        }
    }

    static void create(Repository<Long, Movie> repo) {
        System.out.println("=== Create ===");

        Movie barbie = new Movie("Barbie", "Greta Gerwig", 2023, 114);   // fara id: il genereaza baza de date
        repo.save(barbie);
        System.out.println("a) Saved movie: " + barbie);

        try {
            repo.save(new Movie("", "Greta Gerwig", 3000, 114));
        } catch (ValidationException e) {
            System.out.println("b) The movie is not valid: " + e.getMessage());
        }

        try {
            repo.save(null);
        } catch (IllegalArgumentException e) {
            System.out.println("c) The movie what needs to be saved cannot be null");
        }
    }


    static void update(Repository<Long, Movie> repo) {
        System.out.println("=== Update ===");
        Long idBarbie = repo.findAll(f -> f.getTitle().equals("Barbie")).getLast().getId();

        Movie inexistent = new Movie("Nimic", "Nimeni", 2000, 90);
        inexistent.setId(9999L);
        if (repo.update(inexistent).isPresent())
            System.out.println("a) Movie does not exist (id 9999): nothing changed");

        Movie invalid = new Movie("Barbie", "Greta Gerwig", 2023, 0);
        invalid.setId(idBarbie);
        try {
            repo.update(invalid);
        } catch (ValidationException e) {
            System.out.println("b) New data is not valid: " + e.getMessage());
        }

        try {
            repo.update(null);
        } catch (IllegalArgumentException e) {
            System.out.println("c) Filmul de modificat nu poate fi null");
        }

        Movie barbie = new Movie("Barbie", "Greta Gerwig", 2023, 120);
        barbie.setId(idBarbie);                                 // id-ul says what row was modified
        if (repo.update(barbie).isEmpty())
            System.out.println("d) Changed movie: " + repo.findOne(idBarbie).get());
    }

    static void delete(Repository<Long, Movie> repo) {
        System.out.println("=== Delete ===");
        Long idBarbie = repo.findAll(f -> f.getTitle().equals("Barbie")).getLast().getId();

        Optional<Movie> sters = repo.delete(idBarbie);
        System.out.println("a) " + (sters.isPresent() ? "Deleted: " + sters.get() : "Movie does not exist"));

        Optional<Movie> dinNou = repo.delete(idBarbie);
        System.out.println("b) " + (dinNou.isPresent() ? "Deleted: " + dinNou.get() : "Movie does not exist (already deleted)"));

        try {
            repo.delete(null);
        } catch (IllegalArgumentException e) {
            System.out.println("c) Id cannot be null");
        }
    }


    static void factoryFile() throws IOException {
        System.out.println("=== Factory ===");
        Properties props = new Properties();
        try (InputStream in = Main.class.getResourceAsStream("/config.properties")) {
            props.load(in);
        }
        props.setProperty("repository.type", "FILE");
        try {
            new RepositoryFactory(props).createMovieRepository(new MovieValidator());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    static void searchByDirector(Repository<Long, Movie> repo) {
        System.out.println("=== Search after director ===");
        if (repo instanceof MovieDBRepository dbRepo)
            System.out.println("a) SQL:    " + dbRepo.findByDirector("Christopher Nolan"));

        List<Movie> filtrate = repo.findAll(f -> f.getDirector().equals("Christopher Nolan"));
        System.out.println("b) stream: " + filtrate);
    }


    static Repository<Long, Movie> createRepository() throws IOException {
        Properties props = new Properties();
        try (InputStream in = Main.class.getResourceAsStream("/config.properties")) {
            props.load(in);
        }
        return new RepositoryFactory(props).createMovieRepository(new MovieValidator());
    }


    static void reports(Repository<Long, Movie> repo) {
        //TODO implement the reports
    }

}