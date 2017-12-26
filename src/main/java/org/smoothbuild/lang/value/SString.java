package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

public class SString extends Value {
  private final HashedDb hashedDb;

  public SString(Type type, HashCode hash, HashedDb hashedDb) {
    super(type, hash);
    checkArgument(type.name().equals("String"));
    this.hashedDb = checkNotNull(hashedDb);
  }

  public String value() {
    return hashedDb.readString(hash());
  }

  @Override
  public String toString() {
    return value();
  }
}
