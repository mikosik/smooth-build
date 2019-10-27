package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;

import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.ConcreteType;

import okio.BufferedSource;

public class Bool extends AbstractValue {
  public Bool(Hash dataHash, ConcreteType type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("Bool"));
  }

  public boolean data() {
    try (BufferedSource source = hashedDb.source(dataHash())) {
      if (source.exhausted()) {
        throw corruptedValueException(
            hash(), "It is Bool which value stored in ValuesDb has zero bytes.");
      }
      byte value = source.readByte();
      if (!source.exhausted()) {
        throw corruptedValueException(
            hash(), "It is Bool which value stored in ValuesDb has more than one byte.");
      }
      switch (value) {
        case 0:
          return false;
        case 1:
          return true;
        default:
          throw corruptedValueException(
              hash(), "It is Bool which value stored in ValuesDb is illegal (=" + value + ").");
      }
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  @Override
  public String toString() {
    return type().name() + "(" + data() + "):" + hash();
  }
}
