package org.smoothbuild.db.objects.marshal;

import java.io.InputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.base.BlobObject;
import org.smoothbuild.lang.base.SBlob;

import com.google.common.hash.HashCode;

public class BlobMarshaller implements ObjectMarshaller<SBlob> {
  private final HashedDb hashedDb;

  public BlobMarshaller(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public SBlob write(byte[] objectBytes) {
    HashCode hash = hashedDb.write(objectBytes);
    return read(hash);
  }

  @Override
  public SBlob read(HashCode hash) {
    return new BlobObject(hash, this);
  }

  public InputStream openInputStream(HashCode hash) {
    return hashedDb.openInputStream(hash);
  }
}
