package seminar7.domain;

import java.io.Serializable;
import java.util.Objects;

/**
 * Base class for all entities that have an identifier
 *
 * @param <ID> - type of the entity identifier
 */
public class Entity<ID> implements Serializable {

    private static final long serialVersionUID = 7331115341259248461L;

    protected ID id;

    /**
     *
     * @return the id of the entity
     * or null - if the id was not set
     */
    public ID getId() {
        return id;
    }

    /**
     *
     * @param id - the new id of the entity
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * two entities are equal if they have the same id
     *
     * @param o - the object to compare with
     * @return true - if o is an Entity with the same id,
     * false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity<?> entity)) return false;
        return Objects.equals(id, entity.id);
    }

    /**
     *
     * @return the hash code computed from the id
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     *
     * @return the string representation of the entity
     */
    @Override
    public String toString() {
        return "Entity{id=" + id + '}';
    }
}
