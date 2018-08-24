package org.smoothbuild.lang.value;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.db.values.ValuesDbException.corruptedHashSequenceException;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.db.values.ValuesDbException.ioException;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.NotEnoughBytesException;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Instantiator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class Array extends AbstractValue {
  private final Instantiator instantiator;

  public Array(HashCode dataHash, ConcreteArrayType arrayType, Instantiator instantiator,
      HashedDb hashedDb) {
    super(dataHash, arrayType, hashedDb);
    this.instantiator = instantiator;
  }

  @Override
  public ConcreteArrayType type() {
    return (ConcreteArrayType) super.type();
  }

  public <T extends Value> Iterable<T> asIterable(Class<T> clazz) {
    ConcreteType elemType = type().elemType();
    Preconditions.checkArgument(clazz.isAssignableFrom(elemType.jType()));
    ImmutableList<Value> values = values();
    for (Value value : values) {
      if (!value.type().equals(elemType)) {
        throw corruptedValueException(hash(), "It is array with type " + type()
            + " but one of its elements has type " + value.type());
      }
    }
    return (ImmutableList<T>) values;
  }

  private ImmutableList<Value> values() {
    try {
      return hashedDb
          .readHashes(dataHash())
          .stream()
          .map(h -> instantiator.instantiate(h))
          .collect(toImmutableList());
    } catch (NotEnoughBytesException e) {
      throw corruptedHashSequenceException(hash());
    } catch (IOException e) {
      throw ioException(e);
    }
  }
}
