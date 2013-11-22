package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

public class CachedBlob extends AbstractValue implements SBlob {
  private final HashedDb hashedDb;

  public CachedBlob(HashedDb hashedDb, HashCode hash) {
    super(Type.BLOB, hash);
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public InputStream openInputStream() {
    return hashedDb.openInputStream(hash());
  }
}
