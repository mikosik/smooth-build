package org.smoothbuild.lang.type;

import static org.smoothbuild.db.values.ValuesDbException.valuesDbException;

import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.hash.HashCode;

public class TypeType extends ConcreteType {
  private final TypesDb typesDb;

  protected TypeType(HashCode dataHash, TypesDb typesDb, HashedDb hashedDb) {
    super(writeHashes(hashedDb, dataHash), dataHash, null, null, "Type", ConcreteType.class,
        hashedDb, typesDb);
    this.typesDb = typesDb;
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
      return typesDb.readFromDataHash(dataHash, hash());
    } catch (IOException e) {
      throw valuesDbException(e);
    }
  }
}
