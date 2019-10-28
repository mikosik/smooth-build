package org.smoothbuild.lang.object.base;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.object.type.BlobType;

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
    Hash dataHash = sink.hash();
    return new Blob(dataHash, type, hashedDb);
  }
}
