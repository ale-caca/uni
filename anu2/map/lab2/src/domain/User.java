package domain;
//clasa User mosteneste Entity pt id de tip Long
/**
 * Reprezinta un utilizator al retelei de socializare, mosteneste Entity
 */
public class User extends Entity<Long>{
    private String firstName;
    private String lastName;

    /**
     * Constructor pentru crearea unui utilizator.
     * @param firstName prenumele utilizatorului
     * @param lastName numele utilizatorului
     */
    public User(String firstName,String lastName) {
        this.firstName = firstName;
        this.lastName=lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
