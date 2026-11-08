package repository;

import domain.Entity;
import domain.validator.Validation;
import domain.validator.ValidationException;

import java.io.*;

/**
 * Abstract generic class that extends InMemoryRepository, providing file persistence.
 * It loads data at startup and rewrites the file after each successful save or delete operation.
 *
 * @param <ID> the type of the identifier
 * @param <E> the type of the stored entity, which must extend Entity
 */
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String fileName;

    /**
     * Constructor for the abstract file repository.
     *
     * @param validator the validator used for entity validation
     * @param fileName the path to the file used for data persistence
     */
    public AbstractFileRepository(Validation<E> validator, String fileName) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }
    /**
     * Extracts and creates an entity from a formatted string line read from the file.
     *
     * @param line the string line representing the entity data
     * @return the constructed entity of type E
     */
    protected abstract E extractEntity(String line);
    /**
     * Converts an entity into a formatted string to be written into the file.
     *
     * @param entity the entity to be converted
     * @return a formatted string representing the entity
     */
    protected abstract String createEntityAsString(E entity);

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                try {
                    E entity = extractEntity(line);
                    super.save(entity);
                } catch (Exception e) {
                    System.err.println("Linie corupta ignorata in " + fileName + " la linia " + lineNumber + ": " + line);
                }
            }
        } catch (FileNotFoundException e) {
            // fisierul nu exista inca, repository-ul ramane gol se va crea la prima salvare
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citirea fisierului " + fileName, e);
        }
    }

    private void writeToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (E entity : findAll()) {
                writer.write(createEntityAsString(entity));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RepositoryException("Eroare la scrierea in fisierul " + fileName, e);
        }
    }

    @Override
    public E save(E entity) throws ValidationException {
        E result = super.save(entity);
        if (result == null) {
            writeToFile();
        }
        return result;
    }

    @Override
    public E delete(ID id) {
        E result = super.delete(id);
        if (result != null) {
            writeToFile();
        }
        return result;
    }
}