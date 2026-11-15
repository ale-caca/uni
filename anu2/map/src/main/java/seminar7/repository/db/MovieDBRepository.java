package seminar7.repository.db;

import seminar7.domain.Movie;
import seminar7.domain.validators.Validator;
import seminar7.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovieDBRepository implements Repository<Long,Movie> {
    private final Validator<Movie> validator;
    private final String url;
    private final String username;
    private final String password;

    public MovieDBRepository(Validator<Movie> validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Movie> findOne(Long id) {
        return Optional.empty();
    }

    @Override
    public Iterable<Movie> findAll() {
        return null;
    }

    @Override
    public Optional<Movie> save(Movie entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Movie> delete(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Movie> update(Movie entity) {
        return Optional.empty();
    }


    public List<Movie> findByDirector(String director) {
        List<Movie> movies = new ArrayList<>();
        // TODO: Implement the logic to retrieve movies by director order by year from the database
        return movies;
    }
}
