package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.BaseEntity;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Generic abstract repository - demonstrates POLYMORPHISM and ABSTRACTION.
 * One algorithm (CRUD on a text file) is reused for every entity type T.
 * Subclasses only supply the file name and a fromLine deserializer.
 *
 * Storage format: one entity per line, fields separated by '|'.
 * Stored under ./data/<file>.txt - this is the "notepad" data store.
 */
public abstract class FileRepository<T extends BaseEntity> {

    private static final String DATA_DIR = "data";
    private final Path filePath;
    private final Function<String, T> deserializer;

    protected FileRepository(String fileName, Function<String, T> deserializer) {
        this.filePath = Paths.get(DATA_DIR, fileName);
        this.deserializer = deserializer;
        ensureFileExists();
    }

    private synchronized void ensureFileExists() {
        try {
            Path dir = Paths.get(DATA_DIR);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            if (!Files.exists(filePath)) Files.createFile(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create data file: " + filePath, e);
        }
    }

    /** Read all entities from the file. Bad lines are skipped (logged) so that a
     *  manual edit / corrupted line does not crash the whole API. */
    public synchronized List<T> findAll() {
        List<T> list = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line == null || line.isBlank()) continue;
                // Strip stray CR or whitespace that some editors leave behind
                String clean = line.replace("\r", "").trim();
                if (clean.isEmpty()) continue;
                try {
                    T entity = deserializer.apply(clean);
                    if (entity != null) list.add(entity);
                } catch (Exception ex) {
                    System.err.println("[FileRepository] Skipped malformed line in "
                            + filePath + " -> " + ex.getClass().getSimpleName()
                            + ": " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read " + filePath, e);
        }
        return list;
    }

    public Optional<T> findById(String id) {
        if (id == null) return Optional.empty();
        return findAll().stream().filter(e -> id.equals(e.getId())).findFirst();
    }

    /** Create. Generates a UUID if id is missing. */
    public synchronized T save(T entity) {
        if (entity.getId() == null || entity.getId().isBlank()) {
            entity.setId(UUID.randomUUID().toString());
        }
        try {
            String line = entity.toLine() + System.lineSeparator();
            Files.write(filePath, line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write " + filePath, e);
        }
        return entity;
    }

    /** Update an existing entity by id. Rewrites the entire file. */
    public synchronized Optional<T> update(T entity) {
        if (entity.getId() == null) return Optional.empty();
        List<T> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(entity.getId())) {
                entity.setCreatedAt(all.get(i).getCreatedAt());
                entity.touch();
                all.set(i, entity);
                found = true;
                break;
            }
        }
        if (!found) return Optional.empty();
        rewriteAll(all);
        return Optional.of(entity);
    }

    // Deletes an item by removing it and rewriting the file
    public synchronized boolean deleteById(String id) {
        List<T> all = findAll();
        int before = all.size();
        all.removeIf(e -> e.getId().equals(id));
        if (all.size() == before) return false;
        rewriteAll(all);
        return true;
    }

    public synchronized long count() {
        return findAll().size();
    }

    // Clears the file and writes the whole list back fresh
    private void rewriteAll(List<T> all) {
        StringBuilder sb = new StringBuilder();
        for (T e : all) sb.append(e.toLine()).append(System.lineSeparator());
        try {
            Files.write(filePath, sb.toString().getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to rewrite " + filePath, e);
        }
    }
}
