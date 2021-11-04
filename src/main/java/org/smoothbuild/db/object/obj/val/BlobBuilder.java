package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.util.io.DataWriter;

import okio.BufferedSink;

public class BlobBuilder implements Closeable {
  private final ObjDb objDb;
  private final HashingBufferedSink sink;

  public BlobBuilder(ObjDb objDb, HashingBufferedSink sink) {
    this.objDb = objDb;
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
      return objDb.newBlob(sink.hash());
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }
}
