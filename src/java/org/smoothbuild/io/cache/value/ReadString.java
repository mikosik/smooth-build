package org.smoothbuild.io.cache.value;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.value.instance.CachedString;
import org.smoothbuild.lang.type.SString;

import com.google.common.hash.HashCode;

public class ReadString implements ReadValue<SString> {
  private final HashedDb hashedDb;

  public ReadString(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  @Override
  public SString read(HashCode hash) {
    return new CachedString(hashedDb, hash);
  }
}