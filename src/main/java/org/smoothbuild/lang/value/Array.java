package org.smoothbuild.lang.value;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.CorruptedValueException;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Instantiator;
import org.smoothbuild.lang.type.Type;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class Array extends Value {
  private final Instantiator instantiator;

  public Array(HashCode dataHash, ArrayType arrayType, Instantiator instantiator,
      HashedDb hashedDb) {
    super(dataHash, arrayType, hashedDb);
    this.instantiator = instantiator;
  }

  @Override
  public ArrayType type() {
    return (ArrayType) super.type();
  }

  public <T extends Value> Iterable<T> asIterable(Class<T> clazz) {
    Type elemType = type().elemType();
    Preconditions.checkArgument(clazz.isAssignableFrom(elemType.jType()));
    ImmutableList<Value> values = hashedDb
        .readHashes(dataHash())
        .stream()
        .map(h -> instantiator.instantiate(h))
        .collect(toImmutableList());
    for (Value value : values) {
      if (!value.type().equals(elemType)) {
        throw new CorruptedValueException(hash(), "It is array with type " + type()
            + " but one of its element has type " + value.type());
      }
    }
    return (ImmutableList<T>) values;
  }

  @Override
  public String toString() {
    String elements = stream(asIterable(Value.class).spliterator(), false)
        .map(Object::toString)
        .collect(joining(", "));
    return "[" + elements + "]";
  }
}
