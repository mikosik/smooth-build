package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.OutputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.lang.type.Type;

public class BlobBuilder extends OutputStream {
  private final Type type;
  private final HashedDb hashedDb;
  private final Marshaller marshaller;

  public BlobBuilder(Type type, HashedDb hashedDb) {
    checkArgument(type.name().equals("Blob"));
    this.type = type;
    this.hashedDb = hashedDb;
    this.marshaller = hashedDb.newMarshaller();
  }

  @Override
  public void write(int b) throws IOException {
    marshaller.write(b);
  }

  @Override
  public void write(byte b[]) throws IOException {
    marshaller.write(b, 0, b.length);
  }

  @Override
  public void write(byte b[], int off, int len) {
    marshaller.write(b, off, len);
  }

  public Blob build() {
    marshaller.close();
    return new Blob(type, marshaller.hash(), hashedDb);
  }
}
