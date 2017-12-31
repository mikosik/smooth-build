package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobType extends Type {
  protected BlobType(HashCode hash, TypeType type, HashedDb hashedDb) {
    super(hash, type, null, "Blob", Blob.class, hashedDb);
  }

  @Override
  public Blob newValue(HashCode hash) {
    return new Blob(hash, this, hashedDb);
  }
}
