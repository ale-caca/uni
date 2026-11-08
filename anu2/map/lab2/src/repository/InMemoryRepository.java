package repository;

import domain.Entity;
import domain.validator.ValidationException;
import domain.validator.Validation;

import java.util.HashMap;
import java.util.Map;
//metoda de salvat datele in memorie
/**
 * Implementare a repo in memorie.
 * @param <ID> tipul identificatorului pentru entitati
 * @param <E> tipul entitatii stocate, care trebuie sa extinda Entity
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {

    private Validation<E> validator;
    protected Map<ID, E> entitati;

    /**
     * Constructor care initializeaza map-ul in memorie si asigneaza validatorul corespunzator entitatii.
     * @param validator obiectul responsabil de verificarea corectitudinii datelor inainte de stocare
     */
    public InMemoryRepository(Validation<E> validator) {
        this.validator = validator;
        entitati = new HashMap<ID, E>();
    }

    //metoda de gasit o entitate
    @Override
    public E findOne(ID id) {
        return entitati.get(id);
    }

    //metoda de returnat toate entitatile
    @Override
    public Iterable<E> findAll() {
        return entitati.values();
    }

    //metoda de salvat o entitate in map: daca exista doar o returnez, altfel se pune in map
    /**
     * Adauga o entitate in repository.
     * @param entity entitatea care trebuie adaugata
     * @return null daca salvarea s-a realizat, sau entitatea daca un element cu acelasi ID exista deja
     * @throws IllegalArgumentException daca entitatea transmisa este nula
     * @throws ValidationException daca atributele entitatii sunt invalide
     */
    @Override
    public E save(E entity) throws ValidationException {

        if (entity == null)
            throw new IllegalArgumentException("entitatea nu poate fi NULL");
        validator.validate(entity);
        if (entitati.containsKey(entity.getId()))
            return entity;
        else {
            entitati.put(entity.getId(), entity);
            return null;
        }


    }
    //metoda de stergere a unei entitati
    /**
     * Sterge entitatea asociata ID-ului dat din memorie.
     * @param id identificatorul entitatii care se doreste a fi stearsa
     * @return entitatea care tocmai a fost stearsa, sau null daca ID-ul nu a fost gasit
     * @throws IllegalArgumentException daca id-ul furnizat este nul
     */
    @Override
    public E delete(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("id nu poate fi NULL");
        }
        return entitati.remove(id);
    }
}
