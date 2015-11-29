package org.smoothbuild.db.values.marshal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ArrayMarshaller<T extends Value> implements ValueMarshaller<Array<T>> {
  private final HashedDb hashedDb;
  private final ArrayType arrayType;
  private final ValueMarshaller<T> elementMarshaller;

  public ArrayMarshaller(HashedDb hashedDb, ArrayType arrayType,
      ValueMarshaller<T> elementMarshaller) {
    this.hashedDb = checkNotNull(hashedDb);
    this.arrayType = checkNotNull(arrayType);
    this.elementMarshaller = checkNotNull(elementMarshaller);
  }

  @Override
  public Array<T> read(HashCode hash) {
    return new Array<>(hash, arrayType, this);
  }

  public ImmutableList<T> readElements(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash)) {
      ImmutableList.Builder<T> builder = ImmutableList.builder();
      int size = unmarshaller.readInt();
      for (int i = 0; i < size; i++) {
        builder.add(elementMarshaller.read(unmarshaller.readHash()));
      }
      return builder.build();
    }
  }

  public Array<T> write(List<? extends Value> elements) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(elements.size());
    for (Value element : elements) {
      marshaller.write(element.hash());
    }
    byte[] bytes = marshaller.getBytes();
    HashCode hash = hashedDb.write(bytes);
    return new Array<>(hash, arrayType, this);
  }
}
