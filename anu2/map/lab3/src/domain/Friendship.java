package domain;

import java.time.LocalDateTime;
import java.util.Objects;
/**
 * It represents a relationship between to users
 */
public class Friendship extends Entity<Long> {
    private User friend1;
    private User friend2;
    LocalDateTime date;//data cand s au imprietenit

    /**
     * The constroctor
     * @param firstFriend primul utilizator (prieten)
     * @param secondFriend al doilea utilizator (prieten)
     */
    public Friendship(User firstFriend, User secondFriend) {
        this.friend1 = firstFriend;
        this.friend2 = secondFriend;
        this.date = LocalDateTime.now();
    }

    public User getFriend1() {
        return friend1;
    }

    public void setFriend1(User friend1) {
        this.friend1 = friend1;
    }

    public User getFriend2() {
        return friend2;
    }

    public void setFriend2(User friend2) {
        this.friend2 = friend2;
    }

    //2 prietenii sunt egale daca au aceeasi 2 utilizatori
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        //cast
        Friendship that = (Friendship) o;
        return Objects.equals(friend1, that.friend1) && Objects.equals(friend2, that.friend2);
    }

    //o prietenie sa aiba acelasi cod in memorie
    @Override
    public int hashCode() {
        return Objects.hash(friend1, friend2);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Id: " + getId() + " | User " + friend1.getId() + " <-> User " + friend2.getId() + " | Data: " + date;
    }
}
