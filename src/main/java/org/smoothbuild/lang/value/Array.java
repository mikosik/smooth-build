package org.smoothbuild.lang.value;

import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

import java.util.List;
import java.util.function.Function;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.lang.type.ArrayType;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class Array extends Value {
  private final Function<HashCode, ? extends Value> valueConstructor;
  private final HashedDb hashedDb;

  public Array(HashCode hash, ArrayType arrayType,
      Function<HashCode, ? extends Value> valueConstructor, HashedDb hashedDb) {
    super(arrayType, hash);
    this.valueConstructor = valueConstructor;
    this.hashedDb = hashedDb;
  }

  public ArrayType type() {
    return (ArrayType) super.type();
  }

  public static Array storeArrayInDb(List<? extends Value> elements, ArrayType arrayType,
      Function<HashCode, ? extends Value> valueConstructor, HashedDb hashedDb) {
    Marshaller marshaller = hashedDb.newMarshaller();
    for (Value element : elements) {
      marshaller.writeHash(element.hash());
    }
    marshaller.close();
    return new Array(marshaller.hash(), arrayType, valueConstructor, hashedDb);
  }

  public <T extends Value> Iterable<T> asIterable(Class<T> clazz) {
    Preconditions.checkArgument(clazz.isAssignableFrom(type().elemType().jType()));
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(hash())) {
      ImmutableList.Builder<T> builder = ImmutableList.builder();
      HashCode elementHash = null;
      while ((elementHash = unmarshaller.tryReadHash()) != null) {
        builder.add((T) valueConstructor.apply(elementHash));
      }
      return builder.build();
    }
  }

  public String toString() {
    String elements = stream(asIterable(Value.class).spliterator(), false)
        .map(Object::toString)
        .collect(joining(", "));
    return "[" + elements + "]";
  }
}
