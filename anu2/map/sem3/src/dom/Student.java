package dom;

import java.util.Objects;

public class Student extends Entity<Integer> implements Comparable<Student>{
    @Override
    public int compareTo(Student o){
        return this.nume.compareTo(o.nume);
    }
    private String nume;
    private float media;

    public String getNume() {
        return nume;
    }
    public float getMedia() {
        return media;
    }

    public Student(String nume, float media) {
        super(null);
        this.nume = nume;
        this.media = media;
    }

    @Override
    public String toString() {
        return "Student{" +
                "nume='" + nume + '\'' +
                ", media=" + media +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Float.compare(media, student.media) == 0 && Objects.equals(nume, student.nume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nume, media);
    }
}
