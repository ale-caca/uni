package service;

import domain.Friendship;
import domain.User;
import repository.InMemoryRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Clasa de service responsabuila cu operatiile pe utilizatori
 */
public class UserSv implements Service<User> {
    private InMemoryRepository<Long, User> repository;
    private InMemoryRepository<Long, Friendship> friendshipRepository;

    public UserSv(InMemoryRepository<Long, User> repository, InMemoryRepository<Long, Friendship> friendshipRepository) {
        this.repository = repository;
        this.friendshipRepository = friendshipRepository;
    }

    //gaseste urmatorul cel mai mare id
    @Override
    public Long genererateID() {
        Iterable<User> users = repository.findAll();
        Long id = 0L;//0L= 0 long
        for (User user : users) {
            if (user.getId() > id)
                id = user.getId();
        }
        id++;
        return id;
    }

    /**
     * Sterge un utilizator dupa ID si elimina toate prieteniile in care acesta este implicat.
     * @param ID identificatorul utilizatorului de sters
     * @return utilizatorul sters, sau null daca nu a fost gasit
     */
    @Override
//    public User delete(Long ID) {
//        //User user = repository.findOne(ID);
//        Iterable<Friendship> friendships = friendshipRepository.findAll();
//        for (Friendship f : friendships) {
//            if (f.getFriend1().getId().equals(ID) || f.getFriend2().getId().equals(ID)) {
//                friendshipRepository.delete(f.getId());
//            }
//        }
//        return repository.delete(ID);
//    }
    public User delete(Long userId) {
        //gasesc utilizatorii inainte ca sa ii pot retunra
        User deletedUser = repository.findOne(userId);

        //prieteniile sunt puse intr o lista
        List<Long> friendshipsToDelete = new ArrayList<>();
        for (Friendship f : friendshipRepository.findAll()) {
            if (f.getFriend1().getId().equals(userId) || f.getFriend2().getId().equals(userId)) {
                friendshipsToDelete.add(f.getId());
            }
        }
        //sterg prieteniile
        for (Long fid : friendshipsToDelete) {
            friendshipRepository.delete(fid);
        }
        repository.delete(userId);
        return deletedUser;
    }

    /**
     * Adauga un nou utilizator in sistem. Generaza automat un ID nou.
     * @param firstName prenumele utilizatorului
     * @param lastName numele utilizatorului
     * @return null daca adaugarea a avut succes, altfel entitatea
     */
    public User save(String firstName, String lastName) {
        Long newID = genererateID();
        User newUser = new User(firstName, lastName);
        newUser.setId(newID);
        return repository.save(newUser);
    }

    /**
     * Returneaza lista de useri.
     * @return o lista cu userii
     */
    @Override
    public Iterable<User> findAll(){
        return repository.findAll();
    }

    /**
     * Returneaza o lista cu toti prietenii unui utilizator specificat.
     * @param ID identificatorul utilizatorului pentru care se cauta prietenii
     * @return o lista (ArrayList) continand obiectele de tip User reprezentand prietenii
     */
    public ArrayList<User> getFriends(Long ID){
        ArrayList<User> friends = new ArrayList<>();
        Iterable<Friendship> friendships = friendshipRepository.findAll();
        for (Friendship f : friendships) {
            if(f.getFriend1().getId().equals(ID)) {
                friends.add(f.getFriend2());
            }
            else if(f.getFriend2().getId().equals(ID)) {
                friends.add(f.getFriend1());
            }
        }
        return friends;
    }
    public User findOne(Long ID) {

        return repository.findOne(ID);
    }
}

