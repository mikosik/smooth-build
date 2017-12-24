package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class NothingType extends Type {
  protected NothingType() {
    super("Nothing", Nothing.class);
  }

  @Override
  public Value newValue(HashCode hash, HashedDb hashedDb) {
    throw new RuntimeException("Cannot create value of type 'Nothing'.");
  }
}
