package org.smoothbuild.vm.bytecode.expr.value;

import java.io.Closeable;
import java.io.IOException;
import okio.BufferedSink;
import org.smoothbuild.common.io.DataWriter;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.hashed.HashingBufferedSink;

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

  public void write(DataWriter dataWriter) throws IOException {
    sink.write(dataWriter);
  }

  @Override
  public void close() throws IOException {
    sink.close();
  }

  public BlobB build() throws BytecodeException, IOException {
    sink.close();
    return bytecodeDb.newBlob(sink.hash());
  }
}
