package org.smoothbuild.lang.object.base;

import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.object.db.ObjectDb;

import okio.BufferedSink;

public class BlobBuilder implements Closeable {
  private final ObjectDb objectDb;
  private final HashingBufferedSink sink;

  public BlobBuilder(ObjectDb objectDb, HashingBufferedSink sink) throws HashedDbException {
    this.objectDb = objectDb;
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
    return wrapException(() -> objectDb.newBlob(sink.hash()));
  }
}
