package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;

import com.google.common.hash.HashCode;

public class ReadArray<T extends Value> implements ReadValue<SArray<T>> {
  private final ReadValue<T> readValue;
  private final Type<?> arrayType;
  private final HashedDb hashedDb;

  public ReadArray(HashedDb hashedDb, Type<?> arrayType, ReadValue<T> valueReader) {
    this.hashedDb = hashedDb;
    this.arrayType = checkNotNull(arrayType);
    this.readValue = checkNotNull(valueReader);
  }

  @Override
  public SArray<T> read(HashCode hash) {
    return new CachedArray<T>(hashedDb, hash, arrayType, readValue);
  }
}