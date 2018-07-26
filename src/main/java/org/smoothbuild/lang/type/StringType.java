package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.STRING;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class StringType extends ConcreteType {
  protected StringType(HashCode dataHash, TypeType type, HashedDb hashedDb, TypesDb typesDb) {
    super(dataHash, type, null, STRING, SString.class, hashedDb, typesDb);
  }

  @Override
  public SString newValue(HashCode dataHash) {
    return new SString(dataHash, this, hashedDb);
  }
}
