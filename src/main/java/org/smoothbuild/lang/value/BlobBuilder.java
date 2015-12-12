package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.db.hashed.HashedDb;

public class BlobBuilder {
  private final HashedDb hashedDb;
  private ByteArrayOutputStream outputStream;
  private boolean closed;

  public BlobBuilder(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.closed = false;
  }

  public OutputStream openOutputStream() {
    checkState(!closed, "Cannot open output stream as close() has been already called.");
    checkState(outputStream == null, "Cannot open output stream twice.");
    outputStream = new ByteArrayOutputStream();
    return outputStream;
  }

  public Blob build() {
    closed = true;
    return Blob.storeBlobInDb(getBytes(), hashedDb);
  }

  private byte[] getBytes() {
    if (outputStream == null) {
      return new byte[] {};
    } else {
      return outputStream.toByteArray();
    }
  }
}
