package seminar7.domain;

public class Movie extends Entity<Long>{
    private String title;
    private String director;
    private int year;
    private int duration;

    public Movie(String title, String director, int year, int duration) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Movie{id=" +id+'\''+
                "title='" + title + '\'' +
                ", director='" + director + '\'' +
                ", year=" + year +
                ", duration=" + duration +
                '}';
    }
}
