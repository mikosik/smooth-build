package org.smoothbuild.db.objects.marshal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.base.ArrayObject;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.hash.HashCode;

public class ArrayReader<T extends SValue> implements ObjectReader<SArray<T>> {
  private final ObjectReader<T> elementReader;
  private final SType<?> arrayType;
  private final HashedDb hashedDb;

  public ArrayReader(HashedDb hashedDb, SType<?> arrayType, ObjectReader<T> elementReader) {
    this.hashedDb = hashedDb;
    this.arrayType = checkNotNull(arrayType);
    this.elementReader = checkNotNull(elementReader);
  }

  @Override
  public SArray<T> read(HashCode hash) {
    return new ArrayObject<T>(hashedDb, hash, arrayType, elementReader);
  }
}