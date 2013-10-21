package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import com.google.common.hash.HashCode;

public class BlobObject implements Hashed {
  private final HashedDb hashedDb;
  private final HashCode hash;

  public BlobObject(HashedDb hashedDb, HashCode hash) {
    this.hashedDb = checkNotNull(hashedDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  public InputStream openInputStream() {
    return hashedDb.openInputStream(hash);
  }
}
