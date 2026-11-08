package service;
//interfata, toate clasele trebuie sa aiba aceste metoda

/**
 * Interfata-toate clasele mostenitaote trebuie sa contina aceste metode
 * @param <E> entitate
 */
public interface Service<E> {
    /**
     * Sterge o entitate pe baza id-ului.
     * @param ID id entitatii care urmeaza sa fie stearsa
     * @return entitatea stearsa, sau null daca entitatea cu ID-ul respectiv nu exista
     */
    E delete(Long ID);

    /**
     * Genereaza un ID unic, incrementat, pentru o noua entitate.
     * @return un ID de tip Long care reprezinta valoarea maxima curenta + 1
     */
    Long genererateID();

    /**
     * Returneaza toate entitatile gestionate de serviciu.
     * @return un obiect iterabil ce contine toate entitatile
     */
    Iterable<E> findAll();

}
