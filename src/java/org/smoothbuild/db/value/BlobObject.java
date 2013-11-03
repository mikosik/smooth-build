package org.smoothbuild.db.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import org.smoothbuild.db.hash.HashedDb;
import org.smoothbuild.plugin.Blob;

import com.google.common.hash.HashCode;

public class BlobObject implements Blob {
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

  @Override
  public InputStream openInputStream() {
    return hashedDb.openInputStream(hash);
  }
}
