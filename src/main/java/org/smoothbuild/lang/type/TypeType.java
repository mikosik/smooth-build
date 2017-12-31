package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.hash.HashCode;

public class TypeType extends Type {
  private final TypesDb typesDb;

  protected TypeType(HashCode dataHash, TypesDb typesDb, HashedDb hashedDb) {
    super(hashedDb.writeHashes(dataHash), dataHash, null, null, "Type", Type.class, hashedDb);
    this.typesDb = typesDb;
  }

  @Override
  public Type newValue(HashCode dataHash) {
    return typesDb.readFromDataHash(dataHash);
  }
}
