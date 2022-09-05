package org.smoothbuild.bytecode.expr.val;

import static org.smoothbuild.bytecode.expr.Helpers.wrapHashedDbExcAsBytecodeDbExc;

import java.io.Closeable;
import java.io.IOException;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.Helpers;
import org.smoothbuild.bytecode.hashed.HashingBufferedSink;
import org.smoothbuild.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.util.io.DataWriter;

import okio.BufferedSink;

public class BlobBBuilder implements Closeable {
  private final BytecodeDb bytecodeDb;
  private final HashingBufferedSink sink;

  public BlobBBuilder(BytecodeDb bytecodeDb, HashingBufferedSink sink) {
    this.bytecodeDb = bytecodeDb;
    this.sink = sink;
  }

  public BufferedSink sink() {
    return sink;
  }

  public void write(DataWriter dataWriter) {
    Helpers.wrapHashedDbExcAsBytecodeDbExc(() -> sink.write(dataWriter));
  }

  @Override
  public void close() throws IOException {
    sink.close();
  }

  public BlobB build() {
    return wrapHashedDbExcAsBytecodeDbExc(this::buildImpl);
  }

  private BlobB buildImpl() throws HashedDbExc {
    try {
      sink.close();
      return bytecodeDb.newBlob(sink.hash());
    } catch (IOException e) {
      throw new HashedDbExc(e);
    }
  }
}
