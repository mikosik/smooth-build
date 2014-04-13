package org.smoothbuild.db.objects.build;

import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.objects.instance.ArrayObject;
import org.smoothbuild.db.objects.read.ReadValue;
import org.smoothbuild.lang.base.Hashed;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class ArrayBuilder<T extends SValue> {
  private final HashedDb hashedDb;
  private final SType<?> arrayType;
  private final ReadValue<T> readValue;
  private final List<T> result;

  public ArrayBuilder(HashedDb hashedDb, SType<?> arrayType, ReadValue<T> valueReader) {
    this.hashedDb = hashedDb;
    this.arrayType = arrayType;
    this.readValue = valueReader;
    this.result = Lists.newArrayList();
  }

  public ArrayBuilder<T> add(T elem) {
    result.add(elem);
    return this;
  }

  public SArray<T> build() {
    return array(result, arrayType, readValue);
  }

  private SArray<T> array(List<T> elements, SType<?> type, ReadValue<T> valueReader) {
    HashCode hash = genericArray(elements);
    return new ArrayObject<T>(hashedDb, hash, type, valueReader);
  }

  private HashCode genericArray(List<? extends Hashed> elements) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(elements);
    return hashedDb.store(marshaller.getBytes());
  }
}
