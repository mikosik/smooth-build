package org.smoothbuild.lang.value;

import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.lang.type.ArrayType;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class Array extends Value {
  private final HashedDb hashedDb;

  public Array(HashCode hash, ArrayType arrayType, HashedDb hashedDb) {
    super(arrayType, hash);
    this.hashedDb = hashedDb;
  }

  @Override
  public ArrayType type() {
    return (ArrayType) super.type();
  }

  public static Array storeArrayInDb(List<? extends Value> elements, ArrayType arrayType,
      HashedDb hashedDb) {
    Marshaller marshaller = hashedDb.newMarshaller();
    for (Value element : elements) {
      marshaller.writeHash(element.hash());
    }
    marshaller.close();
    return arrayType.newValue(marshaller.hash(), hashedDb);
  }

  public <T extends Value> Iterable<T> asIterable(Class<T> clazz) {
    Preconditions.checkArgument(clazz.isAssignableFrom(type().elemType().jType()));
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(hash())) {
      ImmutableList.Builder<T> builder = ImmutableList.builder();
      HashCode elementHash = null;
      while ((elementHash = unmarshaller.tryReadHash()) != null) {
        builder.add((T) type().elemType().newValue(elementHash, hashedDb));
      }
      return builder.build();
    }
  }

  @Override
  public String toString() {
    String elements = stream(asIterable(Value.class).spliterator(), false)
        .map(Object::toString)
        .collect(joining(", "));
    return "[" + elements + "]";
  }
}
