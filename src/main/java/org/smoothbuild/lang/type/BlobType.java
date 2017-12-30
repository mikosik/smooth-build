package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobType extends Type {
  protected BlobType(HashCode hash, TypeType type) {
    super(hash, type, "Blob", Blob.class);
  }

  @Override
  public Blob newValue(HashCode hash, HashedDb hashedDb) {
    return new Blob(hash, this, hashedDb);
  }
}
