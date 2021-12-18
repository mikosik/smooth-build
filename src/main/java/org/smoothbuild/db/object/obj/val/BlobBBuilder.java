package org.smoothbuild.db.object.obj.val;

import static org.smoothbuild.db.object.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.object.obj.ByteDb;
import org.smoothbuild.util.io.DataWriter;

import okio.BufferedSink;

public class BlobBBuilder implements Closeable {
  private final ByteDb byteDb;
  private final HashingBufferedSink sink;

  public BlobBBuilder(ByteDb byteDb, HashingBufferedSink sink) {
    this.byteDb = byteDb;
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

  public BlobB build() {
    return wrapHashedDbExceptionAsObjectDbException(this::buildImpl);
  }

  private BlobB buildImpl() throws HashedDbExc {
    try {
      sink.close();
      return byteDb.newBlob(sink.hash());
    } catch (IOException e) {
      throw new HashedDbExc(e);
    }
  }
}
