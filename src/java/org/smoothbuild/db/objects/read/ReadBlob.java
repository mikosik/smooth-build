package org.smoothbuild.db.objects.read;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.instance.CachedBlob;
import org.smoothbuild.lang.base.SBlob;

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