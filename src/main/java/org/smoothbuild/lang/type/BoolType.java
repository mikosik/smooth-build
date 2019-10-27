package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.BOOL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Bool;

public class BoolType extends ConcreteType {
  public BoolType(Hash dataHash, TypeType type, HashedDb hashedDb, ValuesDb valuesDb) {
    super(dataHash, type, null, BOOL, Bool.class, hashedDb, valuesDb);
  }

  @Override
  public Bool newValue(Hash dataHash) {
    return new Bool(dataHash, this, hashedDb);
  }
}
