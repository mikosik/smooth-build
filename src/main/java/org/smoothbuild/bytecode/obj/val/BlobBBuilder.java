package org.smoothbuild.bytecode.obj.val;

import static org.smoothbuild.bytecode.obj.Helpers.wrapHashedDbExceptionAsObjectDbException;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.bytecode.obj.ByteDbImpl;
import org.smoothbuild.db.HashingBufferedSink;
import org.smoothbuild.db.exc.HashedDbExc;
import org.smoothbuild.util.io.DataWriter;

import okio.BufferedSink;

public class BlobBBuilder implements Closeable {
  private final ByteDbImpl byteDb;
  private final HashingBufferedSink sink;

  public BlobBBuilder(ByteDbImpl byteDb, HashingBufferedSink sink) {
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
