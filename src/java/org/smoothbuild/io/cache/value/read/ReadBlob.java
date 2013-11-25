package org.smoothbuild.io.cache.value.read;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.value.instance.CachedBlob;
import org.smoothbuild.lang.type.SBlob;

import com.google.common.hash.HashCode;

public class ReadBlob implements ReadValue<SBlob> {
  private final HashedDb hashedDb;

  public ReadBlob(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  @Override
  public SBlob read(HashCode hash) {
    return new CachedBlob(hashedDb, hash);
  }
}