package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeNames.BLOB;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobType extends ConcreteType {
  public BlobType(HashCode dataHash, TypeType type, HashedDb hashedDb, ValuesDb valuesDb) {
    super(dataHash, type, null, BLOB, Blob.class, hashedDb, valuesDb);
  }

  @Override
  public Blob newValue(HashCode dataHash) {
    return new Blob(dataHash, this, hashedDb);
  }
}
