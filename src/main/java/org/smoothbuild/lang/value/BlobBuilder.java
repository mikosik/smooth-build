package org.smoothbuild.lang.value;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.lang.type.BlobType;

import com.google.common.hash.HashCode;

import okio.BufferedSink;

public class BlobBuilder implements Closeable {
  private final BlobType type;
  private final HashedDb hashedDb;
  private final Marshaller marshaller;

  public BlobBuilder(BlobType type, HashedDb hashedDb) throws IOException {
    this.type = type;
    this.hashedDb = hashedDb;
    this.marshaller = hashedDb.newMarshaller();
  }

  public BufferedSink sink() {
    return marshaller.sink();
  }

  @Override
  public void close() throws IOException {
    marshaller.close();
  }

  public Blob build() throws IOException {
    close();
    HashCode dataHash = marshaller.hash();
    return new Blob(dataHash, type, hashedDb);
  }
}
