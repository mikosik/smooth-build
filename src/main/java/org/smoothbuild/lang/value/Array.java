package org.smoothbuild.lang.value;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;

import java.io.EOFException;
import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Instantiator;

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
    assertIsIterableAs(clazz);
    ImmutableList<Value> values = values();
    for (Value value : values) {
      if (!value.type().equals(type().elemType())) {
        throw corruptedValueException(hash(), "It is array with type " + type().q()
            + " but one of its elements has type " + value.type().q());
      }
    }
    return (ImmutableList<T>) values;
  }

  private <T extends Value> void assertIsIterableAs(Class<T> clazz) {
    ConcreteType elemType = type().elemType();
    if (!(elemType.isNothing() || clazz.isAssignableFrom(elemType.jType()))) {
      throw new IllegalArgumentException("Array of type " + type().q() + " cannot be iterated as " +
          Struct.class.getCanonicalName());
    }
  }

  private ImmutableList<Value> values() {
    try {
      return hashedDb
          .readHashes(dataHash())
          .stream()
          .map(instantiator::instantiate)
          .collect(toImmutableList());
    } catch (EOFException e) {
      throw corruptedValueException(hash(),
          "It is an Array which value stored in ValuesDb number of bytes which is not multiple of" +
              " hash size = " + Hash.size() + ".");
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }
}
