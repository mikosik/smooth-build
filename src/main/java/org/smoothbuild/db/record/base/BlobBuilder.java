package org.smoothbuild.db.record.base;

import static org.smoothbuild.db.record.db.Helpers.wrapException;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.record.db.RecordDb;

import okio.BufferedSink;

public class BlobBuilder implements Closeable {
  private final RecordDb recordDb;
  private final HashingBufferedSink sink;

  public BlobBuilder(RecordDb recordDb, HashingBufferedSink sink) {
    this.recordDb = recordDb;
    this.sink = sink;
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
    return wrapException(() -> recordDb.newBlob(sink.hash()));
  }
}
