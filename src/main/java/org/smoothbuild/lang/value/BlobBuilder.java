package org.smoothbuild.lang.value;

import java.io.IOException;
import java.io.OutputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;

public class BlobBuilder extends OutputStream {
  private final HashedDb hashedDb;
  private final Marshaller outputStream;

  public BlobBuilder(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.outputStream = hashedDb.newMarshaller();
  }

  @Override
  public void write(int b) throws IOException {
    outputStream.write(b);
  }

  @Override
  public void write(byte b[]) throws IOException {
    outputStream.write(b, 0, b.length);
  }

  @Override
  public void write(byte b[], int off, int len) {
    outputStream.write(b, off, len);
  }

  public Blob build() {
    outputStream.close();
    return new Blob(outputStream.hash(), hashedDb);
  }
}
