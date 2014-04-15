package org.smoothbuild.db.objects.marshal;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.base.BlobObject;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SBlob;

import com.google.common.hash.HashCode;

public class BlobWriter implements BlobBuilder {
  private final HashedDb hashedDb;

  private ByteArrayOutputStream outputStream;

  public BlobWriter(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  @Override
  public OutputStream openOutputStream() {
    checkState(this.outputStream == null, "Cannot open output stream twice.");
    this.outputStream = new ByteArrayOutputStream();
    return outputStream;
  }

  @Override
  public SBlob build() {
    checkState(outputStream != null, "No content available. Create one via openOutputStream()");
    return writeBlob(outputStream.toByteArray());
  }

  private BlobObject writeBlob(byte[] objectBytes) {
    HashCode hash = hashedDb.write(objectBytes);
    return new BlobObject(hashedDb, hash);
  }
}
