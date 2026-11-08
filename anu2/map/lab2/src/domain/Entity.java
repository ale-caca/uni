package domain;

import java.util.Objects;

//clasa generica

/**
 * Clasa care defineste un sablon pentru user si prietenii
 * @param <ID> id-ul unic
 */
public class Entity <ID> {
	private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    //suprascrieam equals si hasCode ca 2 obiecte sa fie egale pe baza de id
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    @Override
    public String toString() {
        return "Entity{id=" + id +"}";
}

}
