package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.STRING;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class StringType extends ConcreteType {
  public StringType(HashCode dataHash, TypeType type, HashedDb hashedDb, ValuesDb valuesDb) {
    super(dataHash, type, null, STRING, SString.class, hashedDb, valuesDb);
  }

  @Override
  public SString newValue(HashCode dataHash) {
    return new SString(dataHash, this, hashedDb);
  }
}
