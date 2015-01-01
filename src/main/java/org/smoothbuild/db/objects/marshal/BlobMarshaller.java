package org.smoothbuild.db.objects.marshal;

import java.io.InputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.value.Blob;

import com.google.common.hash.HashCode;

public class BlobMarshaller implements ObjectMarshaller<Blob> {
  private final HashedDb hashedDb;

  public BlobMarshaller(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public Blob write(byte[] objectBytes) {
    HashCode hash = hashedDb.write(objectBytes);
    return read(hash);
  }

  @Override
  public Blob read(HashCode hash) {
    return new Blob(hash, this);
  }

  public InputStream openInputStream(HashCode hash) {
    return hashedDb.openInputStream(hash);
  }
}
