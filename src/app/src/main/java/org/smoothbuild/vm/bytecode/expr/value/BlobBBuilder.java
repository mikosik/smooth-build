package org.smoothbuild.vm.bytecode.expr.value;

import java.io.Closeable;
import java.io.IOException;
import okio.BufferedSink;
import okio.Okio;
import org.smoothbuild.common.io.DataWriter;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.hashed.HashingSink;

public class BlobBBuilder implements Closeable {
  private final BytecodeDb bytecodeDb;
  private final HashingSink sink;
  private final BufferedSink bufferedSink;

  public BlobBBuilder(BytecodeDb bytecodeDb, HashingSink sink) {
    this.bytecodeDb = bytecodeDb;
    this.sink = sink;
    this.bufferedSink = Okio.buffer(sink);
  }

  public BufferedSink sink() {
    return bufferedSink;
  }

  public void write(DataWriter dataWriter) throws IOException {
    dataWriter.writeTo(bufferedSink);
  }

  @Override
  public void close() throws IOException {
    bufferedSink.close();
  }

  public BlobB build() throws BytecodeException, IOException {
    bufferedSink.close();
    return bytecodeDb.newBlob(sink.hash());
  }
}
