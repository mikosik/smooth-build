package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.BOOL;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Bool;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class BoolType extends ConcreteType {
  protected BoolType(HashCode dataHash, TypeType type, HashedDb hashedDb, TypesDb typesDb) {
    super(dataHash, type, null, BOOL, Bool.class, hashedDb, typesDb);
  }

  @Override
  public Bool newValue(HashCode dataHash) {
    return new Bool(dataHash, this, hashedDb);
  }
}
