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
      HashCode elementHash = null;
      while ((elementHash = unmarshaller.tryReadHash()) != null) {
        builder.add(elementMarshaller.read(elementHash));
      }
      return builder.build();
    }
  }

  public Array<T> write(List<? extends Value> elements) {
    Marshaller marshaller = new Marshaller(hashedDb);
    for (Value element : elements) {
      marshaller.write(element.hash());
    }
    HashCode hash = marshaller.close();
    return new Array<>(hash, arrayType, this);
  }
}
