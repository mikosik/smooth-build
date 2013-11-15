package org.smoothbuild.io.db.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import org.smoothbuild.io.db.hash.HashedDb;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.plugin.Blob;

import com.google.common.hash.HashCode;

public class BlobObject implements Blob {
  private final HashedDb hashedDb;
  private final HashCode hash;

  public BlobObject(HashedDb hashedDb, HashCode hash) {
    this.hashedDb = checkNotNull(hashedDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public Type type() {
    return Type.BLOB;
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
