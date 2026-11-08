package dom;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepo <ID, E extends Entity<ID>> implements Repository0<ID,E> {
    private Map<ID,E> map;
    public InMemoryRepo(Map<ID,E> map){
        this.map = map;
    }

    @Override
    public E findOne(ID id) {
        return null;
    }

    @Override
    public Iterable<E> findAll() {
        return null;
    }

    @Override
    public E save(E entity) {
        return null;
    }

    @Override
    public E delete(ID id) {
        return null;
    }

    @Override
    public E update(E entity) {
        return null;
    }
}
