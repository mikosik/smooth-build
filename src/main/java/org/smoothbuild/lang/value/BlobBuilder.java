package org.smoothbuild.lang.value;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.type.BlobType;

import com.google.common.hash.HashCode;

import okio.BufferedSink;

public class BlobBuilder implements Closeable {
  private final BlobType type;
  private final HashedDb hashedDb;
  private final HashingBufferedSink sink;

  public BlobBuilder(BlobType type, HashedDb hashedDb) throws IOException {
    this.type = type;
    this.hashedDb = hashedDb;
    this.sink = hashedDb.sink();
  }

  public BufferedSink sink() {
    return sink;
  }

  @Override
  public void close() throws IOException {
    sink.close();
  }

  public Blob build() throws IOException {
    close();
    HashCode dataHash = sink.hash();
    return new Blob(dataHash, type, hashedDb);
  }
}
