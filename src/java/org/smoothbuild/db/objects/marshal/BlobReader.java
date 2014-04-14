package org.smoothbuild.db.objects.marshal;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.base.BlobObject;
import org.smoothbuild.lang.base.SBlob;

import com.google.common.hash.HashCode;

public class BlobReader implements ObjectReader<SBlob> {
  private final HashedDb hashedDb;

  public BlobReader(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  @Override
  public SBlob read(HashCode hash) {
    return new BlobObject(hashedDb, hash);
  }
}