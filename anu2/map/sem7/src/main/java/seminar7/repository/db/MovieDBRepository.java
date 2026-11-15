package seminar7.repository.db;

import seminar7.domain.Movie;
import seminar7.domain.validators.Validator;
import seminar7.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import seminar7.repository.RepositoryException;

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
        List<Movie> movies=new ArrayList();
        try(Connection connection= DriverManager.getConnection(url,username,password);
            PreparedStatement ps=connection.prepareStatement("select * from filme by id");
            ResultSet rs=ps.executeQuery()){
            while(rs.next()){
                Movie m=new Movie(rs.getString("titlu"),
                        rs.getString("regizor"),
                        rs.getInt("an"),
                        rs.getInt("durata"));
                m.setId(rs.getLong("id"));
                movies.add(m);
            }
        }catch(SQLException e){
            throw new RepositoryException("Error while reading from the database:"+e.getMessage(),e);
        }
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
