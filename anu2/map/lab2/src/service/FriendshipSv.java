package service;

import domain.Friendship;
import domain.User;
import repository.InMemoryRepository;

/**
 * Clasa de service responsabila cu gestionarea operatiilor specifice relatiilor de prietenie.
 */
public class FriendshipSv implements Service<Friendship> {
    InMemoryRepository<Long, Friendship> repository;
    InMemoryRepository<Long, User> userRepository;

    /**
     * Constructor pentru serviciul de prietenii.
     * @param repository repository-ul principal unde sunt stocate prieteniile
     * @param userRepository repository-ul secundar de utilizatori necesar pentru a asocia obiectele de tip User
     */
    public FriendshipSv(InMemoryRepository<Long, Friendship> repository, InMemoryRepository<Long, User> userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }


    @Override
    public Long genererateID() {
        Iterable<Friendship> friendships = repository.findAll();
        Long id = 0L;
        for (Friendship friendship : friendships) {
            if (friendship.getId() > id)
                id = friendship.getId();
        }
        id++;
        return id;
    }

    /**
     * Sterge o relatie de prietenie existenta din repository.
     * @param ID identificatorul prieteniei de sters
     * @return prietenia stearsa, sau null daca nu exista
     */
    @Override
    public Friendship delete(Long ID) {
        return repository.delete(ID);
    }

    /**
     * Creeaza si salveaza o noua relatie de prietenie intre doi utilizatori existenti.
     * @param ID1 identificatorul primului utilizator
     * @param ID2 identificatorul celui de-al doilea utilizator
     * @return null daca prietenia a fost salvata cu succes, sau obiectul Friendship daca acesta exista deja
     */
    public Friendship save(Long ID1, Long ID2) {
        Long newID=genererateID();
        User user1 = userRepository.findOne(ID1);
        User user2 = userRepository.findOne(ID2);
        Friendship friendship = new Friendship(user1, user2);
        friendship.setId(newID);
        return repository.save(friendship);
    }

    @Override
    public Iterable<Friendship> findAll() {
        return repository.findAll();
    }
}
