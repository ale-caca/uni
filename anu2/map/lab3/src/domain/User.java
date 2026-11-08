package domain;
//clasa User mosteneste Entity pt id de tip Long
/**
 * It is an user of the social platform
 */
public class User extends Entity<Long>{
    private String firstName;
    private String lastName;

    /**
     * The constroctor that creates the entity
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

    /**
     * Deserializes a CSV-style line into a User entity.
     *
     * @param line string containing "id;firstName;lastName"
     * @return the constructed User entity
     */
    public static User fromString(String line){
        String[] fields = line.split(";",-1);
        User user = new User(fields[1],fields[2]);
        user.setId(Long.parseLong(fields[0]));
        return user;
    }

    /**
     * Serializes a User entity into a CSV-style line.
     *
     * @param user the entity to serialize
     * @return formatted line "id;firstName;lastName"
     */
    public static String toFileLine(User user){
            return user.getId() + ";" + user.getFirstName() + ";" + user.getLastName();
    }

    @Override
    public String toString(){
        return getId() + " " +firstName + ";" + lastName;
    }
}
