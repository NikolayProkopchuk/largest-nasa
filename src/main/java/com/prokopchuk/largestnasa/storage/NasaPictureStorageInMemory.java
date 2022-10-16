package com.prokopchuk.largestnasa.storage;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class NasaPictureStorageInMemory implements NasaPictureStorage {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<byte[]> getPictureByKey(String key) {
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public void save(String id, byte[] image) {
        storage.put(id, image);
    }
}
