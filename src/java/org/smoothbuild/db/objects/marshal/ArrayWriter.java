package org.smoothbuild.db.objects.marshal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.objects.base.ArrayObject;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.Hashed;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;

public class ArrayWriter<T extends SValue> implements ArrayBuilder<T> {
  private final HashedDb hashedDb;
  private final SArrayType<T> arrayType;
  private final ObjectReader<T> elementReader;
  private final List<T> result;

  public ArrayWriter(HashedDb hashedDb, SArrayType<T> arrayType, ObjectReader<T> elementReader) {
    this.hashedDb = hashedDb;
    this.arrayType = arrayType;
    this.elementReader = elementReader;
    this.result = Lists.newArrayList();
  }

  @Override
  public ArrayBuilder<T> add(T elem) {
    checkNotNull(elem);
    checkArgument(elem.type().equals(arrayType.elemType()));
    result.add(elem);
    return this;
  }

  @Override
  public SArray<T> build() {
    return array(result, arrayType, elementReader);
  }

  private SArray<T> array(List<T> elements, SType<?> type, ObjectReader<T> elementReader) {
    HashCode hash = genericArray(elements);
    return new ArrayObject<T>(hashedDb, hash, type, elementReader);
  }

  private HashCode genericArray(List<? extends Hashed> elements) {
    return hashedDb.write(marshalArray(elements));
  }

  public static byte[] marshalArray(List<? extends Hashed> elements) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(elements);
    return marshaller.getBytes();
  }
}
