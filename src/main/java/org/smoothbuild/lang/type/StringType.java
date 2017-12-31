package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class StringType extends Type {
  protected StringType(HashCode hash, TypeType type, HashedDb hashedDb) {
    super(hash, type, null, "String", SString.class, hashedDb);
  }

  @Override
  public SString newValue(HashCode hash) {
    return new SString(hash, this, hashedDb);
  }
}
