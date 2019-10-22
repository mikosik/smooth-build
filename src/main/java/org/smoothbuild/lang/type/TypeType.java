package org.smoothbuild.lang.type;

import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;

import com.google.common.hash.HashCode;

public class TypeType extends ConcreteType {
  private final ValuesDb valuesDb;

  public TypeType(HashCode dataHash, ValuesDb valuesDb, HashedDb hashedDb) {
    super(writeHashes(hashedDb, dataHash), dataHash, null, null, "Type", ConcreteType.class,
        hashedDb, valuesDb);
    this.valuesDb = valuesDb;
  }

  private static HashCode writeHashes(HashedDb hashedDb, HashCode dataHash) {
    try {
      return hashedDb.writeHashes(dataHash);
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }

  @Override
  public ConcreteType type() {
    return this;
  }

  @Override
  public ConcreteType newValue(HashCode dataHash) {
    try {
      return valuesDb.readFromDataHash(dataHash, hash());
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }
}
