package org.smoothbuild.lang.value;

import static com.google.common.collect.ImmutableList.toImmutableList;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.CorruptedValueException;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Instantiator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class Array extends AbstractValue {
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
    ConcreteType elemType = type().elemType();
    Preconditions.checkArgument(clazz.isAssignableFrom(elemType.jType()));
    ImmutableList<Value> values = hashedDb
        .readHashes(dataHash())
        .stream()
        .map(h -> instantiator.instantiate(h))
        .collect(toImmutableList());
    for (Value value : values) {
      if (!value.type().equals(elemType)) {
        throw new CorruptedValueException(hash(), "It is array with type " + type()
            + " but one of its elements has type " + value.type());
      }
    }
    return (ImmutableList<T>) values;
  }
}
