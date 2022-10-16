package com.prokopchuk.largestnasa.storage;

import java.util.Optional;

public interface NasaPictureStorage {

    Optional<byte[]> getPictureByKey(String key);

    void save(String id, byte[] image);
}
