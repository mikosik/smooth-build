package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.hash.HashCode;

public class TypeType extends ConcreteType {
  private final TypesDb typesDb;

  protected TypeType(HashCode dataHash, TypesDb typesDb, HashedDb hashedDb) {
    super(hashedDb.writeHashes(dataHash), dataHash, null, null, "Type", ConcreteType.class,
        hashedDb, typesDb);
    this.typesDb = typesDb;
  }

  @Override
  public ConcreteType type() {
    return this;
  }

  @Override
  public ConcreteType newValue(HashCode dataHash) {
    return typesDb.readFromDataHash(dataHash, hash());
  }
}
