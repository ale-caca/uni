package repository;

import domain.Entity;
/**
 * Interfata pentru operatii CRUD pe un repository.
 * @param <ID> tipul identificatorului
 * @param <E> tipul entitatii (trebuie sa extinda Entity)
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * Cauta o entitate pe baza ID-ului.
     * @param id identificatorul entitatii cautate
     * @return entitatea cu ID-ul specificat, sau null daca nu exista
     */
    E findOne(ID id);
    /**
     * Returneaza toate entitatile.
     * @return un obiect iterabil ce contine toate entitatile
     */
    Iterable<E> findAll();
    /**
     * Salveaza o entitate.
     * @param entity entitatea care trebuie salvata
     * @return null daca salvarea a reusit, sau entitatea daca aceasta exista deja
     */
    E save(E entity);
    /**
     * Sterge o entitate dupa ID.
     * @param id identificatorul entitatii care trebuie stearsa
     * @return entitatea stearsa, sau null daca nu exista o entitate cu acest ID
     */
    E delete(ID id);
}

