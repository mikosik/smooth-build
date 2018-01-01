package org.smoothbuild.lang.value;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

import java.util.List;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;

public class Array extends Value {
  public Array(HashCode hash, ArrayType arrayType, HashedDb hashedDb) {
    super(hash, arrayType, hashedDb);
  }

  @Override
  public ArrayType type() {
    return (ArrayType) super.type();
  }

  public static Array storeArrayInDb(List<? extends Value> elements, ArrayType arrayType,
      HashedDb hashedDb) {
    HashCode[] elementHashes = elements.stream().map(Value::hash).toArray(HashCode[]::new);
    return arrayType.newValue(hashedDb.writeHashes(elementHashes));
  }

  public <T extends Value> Iterable<T> asIterable(Class<T> clazz) {
    Preconditions.checkArgument(clazz.isAssignableFrom(type().elemType().jType()));
    Type elemType = type().elemType();
    return hashedDb.readHashes(hash())
        .stream()
        .map(h -> (T) elemType.newValue(h))
        .collect(toImmutableList());
  }

  @Override
  public String toString() {
    String elements = stream(asIterable(Value.class).spliterator(), false)
        .map(Object::toString)
        .collect(joining(", "));
    return "[" + elements + "]";
  }
}
