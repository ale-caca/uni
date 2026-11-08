package repository;

import domain.Entity;
import domain.validator.Validation;
import domain.validator.ValidationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Generic file repository based on composition instead of inheritance.
 * Reads and writes entities using modern Java NIO Streams and functional mappers.
 *
 * @param <ID> the entity identifier type
 * @param <E> the entity type
 */
public class FileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private final String fileName;
    private final Function<String, E> lineToEntity;
    private final Function<E, String> entityToLine;

    /**
     * Constructor for FileRepository.
     *
     * @param validator the entity validator
     * @param fileName path to the data storage file
     * @param lineToEntity functional mapper converting a raw text line into an entity
     * @param entityToLine functional mapper converting an entity into a formatted text line
     */
    public FileRepository(Validation<E> validator,
                          String fileName,
                          Function<String, E> lineToEntity,
                          Function<E, String> entityToLine) {
        super(validator);
        this.fileName = Objects.requireNonNull(fileName, "Numele fisierului nu poate fi null!");
        this.lineToEntity = Objects.requireNonNull(lineToEntity, "Functia lineToEntity nu poate fi null!");
        this.entityToLine = Objects.requireNonNull(entityToLine, "Functia entityToLine nu poate fi null!");
        loadData();
    }

    private void loadData() {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            return;
        }

        try (Stream<String> linesStream = Files.lines(path)) {
            List<String> lines = linesStream.collect(Collectors.toList());
            IntStream.range(0, lines.size())
                    .forEach(index -> {
                        String line = lines.get(index);
                        int lineNumber = index + 1;
                        if (!line.isBlank()) {
                            try {
                                E entity = lineToEntity.apply(line);
                                super.save(entity);
                            } catch (Exception e) {
                                System.err.println("Linie corupta ignorata in " + fileName + " la linia " + lineNumber + ": " + line);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citirea fisierului " + fileName, e);
        }
    }

    private void writeToFile() {
        Path path = Paths.get(fileName);
        List<String> lines = StreamSupport.stream(findAll().spliterator(), false)
                .map(entityToLine)
                .collect(Collectors.toList());
        try {
            Files.write(path, lines);
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