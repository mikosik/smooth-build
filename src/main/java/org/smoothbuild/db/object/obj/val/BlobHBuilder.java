package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.util.io.DataWriter;

import okio.BufferedSink;

public class BlobHBuilder implements Closeable {
  private final ObjectHDb objectHDb;
  private final HashingBufferedSink sink;

  public BlobHBuilder(ObjectHDb objectHDb, HashingBufferedSink sink) {
    this.objectHDb = objectHDb;
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

  public BlobH build() {
    return wrapHashedDbExceptionAsObjectDbException(this::buildImpl);
  }

  private BlobH buildImpl() throws HashedDbException {
    try {
      sink.close();
      return objectHDb.newBlob(sink.hash());
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }
}
