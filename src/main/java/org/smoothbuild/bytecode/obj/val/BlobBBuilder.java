package org.smoothbuild.bytecode.obj.val;

import static org.smoothbuild.bytecode.obj.Helpers.wrapHashedDbExcAsObjDbExc;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.db.HashingBufferedSink;
import org.smoothbuild.db.exc.HashedDbExc;
import org.smoothbuild.util.io.DataWriter;

import okio.BufferedSink;

public class BlobBBuilder implements Closeable {
  private final ObjDbImpl byteDb;
  private final HashingBufferedSink sink;

  public BlobBBuilder(ObjDbImpl byteDb, HashingBufferedSink sink) {
    this.byteDb = byteDb;
    this.sink = sink;
  }

  public BufferedSink sink() {
    return sink;
  }

  public void write(DataWriter dataWriter) {
    wrapHashedDbExcAsObjDbExc(() -> sink.write(dataWriter));
  }

  @Override
  public void close() throws IOException {
    sink.close();
  }

  public BlobB build() {
    return wrapHashedDbExcAsObjDbExc(this::buildImpl);
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
