package org.smoothbuild.lang.value;

import java.io.IOException;
import java.io.OutputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.lang.type.BlobType;

import com.google.common.hash.HashCode;

public class BlobBuilder extends OutputStream {
  private final BlobType type;
  private final HashedDb hashedDb;
  private final Marshaller marshaller;
  private final OutputStream outputStream;

  public BlobBuilder(BlobType type, HashedDb hashedDb) {
    this.type = type;
    this.hashedDb = hashedDb;
    this.marshaller = hashedDb.newMarshaller();
    this.outputStream = marshaller.sink().outputStream();
  }

  @Override
  public void write(int b) {
    try {
      outputStream.write(b);
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
  }

  @Override
  public void write(byte b[]) {
    this.write(b, 0, b.length);
  }

  @Override
  public void write(byte b[], int off, int len) {
    try {
      outputStream.write(b, off, len);
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
  }

  public Blob build() {
    try {
      outputStream.close();
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
    HashCode dataHash = marshaller.hash();
    return new Blob(dataHash, type, hashedDb);
  }

  private void rethrowAsHashedDbException(Throwable e) {
    throw new HashedDbException("IO error occurred while writing object.", e);
  }
}
