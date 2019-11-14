package org.smoothbuild.lang.object.base;

import static org.smoothbuild.lang.object.db.Helpers.wrapException;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.lang.object.db.ObjectsDb;

import okio.BufferedSink;

public class BlobBuilder implements Closeable {
  private final ObjectsDb objectsDb;
  private final HashingBufferedSink sink;

  public BlobBuilder(ObjectsDb objectsDb, HashingBufferedSink sink) throws HashedDbException {
    this.objectsDb = objectsDb;
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
    return wrapException(() -> objectsDb.newBlobSObject(sink.hash()));
  }
}
