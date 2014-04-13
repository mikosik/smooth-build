package org.smoothbuild.db.objects.read;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.instance.CachedArray;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.hash.HashCode;

public class ReadArray<T extends SValue> implements ReadValue<SArray<T>> {
  private final ReadValue<T> readValue;
  private final SType<?> arrayType;
  private final HashedDb hashedDb;

  public ReadArray(HashedDb hashedDb, SType<?> arrayType, ReadValue<T> valueReader) {
    this.hashedDb = hashedDb;
    this.arrayType = checkNotNull(arrayType);
    this.readValue = checkNotNull(valueReader);
  }

  @Override
  public SArray<T> read(HashCode hash) {
    return new CachedArray<T>(hashedDb, hash, arrayType, readValue);
  }
}