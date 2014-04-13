package org.smoothbuild.db.objects.read;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.instance.CachedString;
import org.smoothbuild.lang.base.SString;

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