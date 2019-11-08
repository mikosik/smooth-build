package org.smoothbuild.lang.object.base;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.object.db.ValuesDb;
import org.smoothbuild.lang.object.db.ValuesDbException;
import org.smoothbuild.lang.object.type.BlobType;

import okio.BufferedSink;

public class BlobBuilder implements Closeable {
  private final BlobType type;
  private final ValuesDb valuesDb;
  private final HashingBufferedSink sink;

  public BlobBuilder(BlobType type, ValuesDb valuesDb) throws ValuesDbException {
    this.type = type;
    this.valuesDb = valuesDb;
    this.sink = valuesDb.sink();
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
    return new Blob(dataHash, type, valuesDb);
  }
}
