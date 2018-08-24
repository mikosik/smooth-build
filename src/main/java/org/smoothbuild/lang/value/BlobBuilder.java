package org.smoothbuild.lang.value;

import java.io.IOException;
import java.io.OutputStream;

import org.smoothbuild.db.hashed.HashedDb;
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
  public void write(int b) throws IOException {
    outputStream.write(b);
  }

  @Override
  public void write(byte b[]) throws IOException {
    this.write(b, 0, b.length);
  }

  @Override
  public void write(byte b[], int off, int len) throws IOException {
    outputStream.write(b, off, len);
  }

  public Blob build() throws IOException {
    outputStream.close();
    HashCode dataHash = marshaller.hash();
    return new Blob(dataHash, type, hashedDb);
  }
}
