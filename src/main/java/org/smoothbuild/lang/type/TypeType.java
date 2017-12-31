package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.hash.HashCode;

public class TypeType extends Type {
  private final TypesDb typesDb;

  protected TypeType(HashCode hash, TypesDb typesDb, HashedDb hashedDb) {
    super(hash, null, null, "Type", Type.class, hashedDb);
    this.typesDb = typesDb;
  }

  @Override
  public Type newValue(HashCode hash, HashedDb hashedDb) {
    return typesDb.read(hash);
  }
}
