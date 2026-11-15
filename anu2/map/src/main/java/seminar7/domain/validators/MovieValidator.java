package seminar7.domain.validators;

import seminar7.domain.Movie;

import java.time.Year;

public class MovieValidator implements Validator<Movie> {
    @Override
    public void validate(Movie entity) {
        StringBuilder errors = new StringBuilder();

        if(entity == null){
            throw new ValidationException("Movie cannot be null!");
        }

        if(entity.getId() != null && entity.getId()<0)
            errors.append("Id invalid;");
        if(entity.getTitle() == null || entity.getTitle().isEmpty())
            errors.append("Title invalid;");
        if(entity.getDirector() == null || entity.getDirector().isEmpty())
            errors.append("Director invalid;");
        if(entity.getYear()<1888 || entity.getYear()> Year.now().getValue())
            errors.append("Invalid year;");
        if(entity.getDuration()<1 || entity.getDuration()>600)
            errors.append("Invalid duration;");

        if(!errors.isEmpty())
            throw new ValidationException(errors.toString());
    }
}
