package org.smoothbuild.lang.type;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobType extends Type {
  protected BlobType(HashCode dataHash, TypeType type, HashedDb hashedDb) {
    super(dataHash, type, null, "Blob", Blob.class, hashedDb);
  }

  @Override
  public Blob newValue(HashCode dataHash) {
    return new Blob(dataHash, this, hashedDb);
  }
}
