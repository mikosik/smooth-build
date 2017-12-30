package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;

import com.google.common.hash.HashCode;

public class TypeType extends Type {
  private final TypesDb typesDb;

  protected TypeType(HashCode hash, TypesDb typesDb) {
    super(hash, null, "Type", Type.class);
    this.typesDb = typesDb;
  }

  @Override
  public Type newValue(HashCode hash, HashedDb hashedDb) {
    return typesDb.read(hash);
  }
}
