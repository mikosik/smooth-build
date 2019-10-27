package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.STRING;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.SString;

public class StringType extends ConcreteType {
  public StringType(Hash dataHash, TypeType type, HashedDb hashedDb, ValuesDb valuesDb) {
    super(dataHash, type, null, STRING, SString.class, hashedDb, valuesDb);
  }

  @Override
  public SString newValue(Hash dataHash) {
    return new SString(dataHash, this, hashedDb);
  }
}
