package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class StringType extends Type {
  protected StringType(HashCode hash, TypeType type) {
    super(hash, type, "String", SString.class);
  }

  @Override
  public SString newValue(HashCode hash, HashedDb hashedDb) {
    return new SString(hash, this, hashedDb);
  }
}
