package org.smoothbuild.io.cache.value.instance;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.BLOB;

import java.io.InputStream;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.lang.base.SBlob;

import com.google.common.hash.HashCode;

public class CachedBlob extends CachedValue implements SBlob {
  private final HashedDb hashedDb;

  public CachedBlob(HashedDb hashedDb, HashCode hash) {
    super(BLOB, hash);
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public InputStream openInputStream() {
    return hashedDb.openInputStream(hash());
  }
}
