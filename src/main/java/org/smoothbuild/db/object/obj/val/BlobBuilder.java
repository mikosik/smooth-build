package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.obj.ObjectDb;
import org.smoothbuild.util.io.DataWriter;

import okio.BufferedSink;

public class BlobBuilder implements Closeable {
  private final ObjectDb objectDb;
  private final HashingBufferedSink sink;

  public BlobBuilder(ObjectDb objectDb, HashingBufferedSink sink) {
    this.objectDb = objectDb;
    this.sink = sink;
  }

  public BufferedSink sink() {
    return sink;
  }

  public void write(DataWriter dataWriter) {
    wrapHashedDbExceptionAsObjectDbException(() -> sink.write(dataWriter));
  }

  @Override
  public void close() throws IOException {
    sink.close();
  }

  public Blob build() {
    return wrapHashedDbExceptionAsObjectDbException(this::buildImpl);
  }

  private Blob buildImpl() throws HashedDbException {
    try {
      sink.close();
      return objectDb.newBlob(sink.hash());
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }
}
