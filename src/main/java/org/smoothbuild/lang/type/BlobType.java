package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.BLOB;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobType extends ConcreteType {
  protected BlobType(HashCode dataHash, TypeType type, HashedDb hashedDb) {
    super(dataHash, type, null, BLOB, Blob.class, hashedDb);
  }

  @Override
  public Blob newValue(HashCode dataHash) {
    return new Blob(dataHash, this, hashedDb);
  }
}
