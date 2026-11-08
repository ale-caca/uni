//package repository;
//
//import domain.Friendship;
//import domain.User;
//import domain.validator.Validation;
//import java.time.LocalDateTime;
//
//public class PrietenieFileRepository extends AbstractFileRepository<Long, Friendship> {
//
//    public PrietenieFileRepository(Validation<Friendship> validator, String fileName) {
//        super(validator, fileName);
//    }
//
//    @Override
//    protected Friendship extractEntity(String line) {
//        String[] fields = line.split(";", -1);
//        Long id1 = Long.parseLong(fields[0]);
//        Long id2 = Long.parseLong(fields[1]);
//        LocalDateTime date = LocalDateTime.parse(fields[2]);
//
//        // Creăm obiecte User schelet, doar cu ID, necesare pentru Friendship
//        User u1 = new User("", "");
//        u1.setId(id1);
//        User u2 = new User("", "");
//        u2.setId(id2);
//
//        Friendship friendship = new Friendship(u1, u2);
//        friendship.setDate(date); // Presupunând că ai adăugat metoda setDate în Friendship
//
//        // Generăm un ID unic combinând cele două ID-uri de utilizator
//        Long id = Math.min(id1, id2) * 100000 + Math.max(id1, id2);
//        friendship.setId(id);
//
//        return friendship;
//    }
//
//    @Override
//    protected String createEntityAsString(Friendship entity) {
//        return entity.getFriend1().getId() + ";" +
//                entity.getFriend2().getId() + ";" +
//                entity.getDate().toString();
//    }
//}

package repository;

import domain.Friendship;
import domain.User;
import domain.validator.Validation;
import java.time.LocalDateTime;

/**
 * File-based repository implementation for Friendship entities.
 */
public class PrietenieFileRepository extends AbstractFileRepository<Long, Friendship> {

    // Contor care asigura ID-uri secventiale normale la citirea din fisier
    private static Long currentId = 1L;

    /**
     * Constructor for the friendship file repository.
     *
     * @param validator the validator for friendship entities
     * @param fileName the name of the file where friendships are stored
     */
    public PrietenieFileRepository(Validation<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected Friendship extractEntity(String line) {
        String[] fields = line.split(";", -1);
        Long id1 = Long.parseLong(fields[0]);
        Long id2 = Long.parseLong(fields[1]);
        LocalDateTime date = LocalDateTime.parse(fields[2]);

        User u1 = new User("", "");
        u1.setId(id1);
        User u2 = new User("", "");
        u2.setId(id2);

        Friendship friendship = new Friendship(u1, u2);
        friendship.setDate(date);
        friendship.setId(currentId++);

        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getFriend1().getId() + ";" +
                entity.getFriend2().getId() + ";" +
                entity.getDate().toString();
    }
}