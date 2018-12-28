package org.smoothbuild.db.hashed;

import static com.google.common.base.Preconditions.checkState;
import static okio.Okio.buffer;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Supplier;

import com.google.common.hash.HashCode;

import okio.BufferedSink;
import okio.Sink;

public class Marshaller implements Closeable {
  private final BufferedSink sink;
  private final Supplier<HashCode> hashSupplier;

  public Marshaller(Sink sink, Supplier<HashCode> hashSupplier) {
    this.sink = buffer(sink);
    this.hashSupplier = hashSupplier;
  }

  public BufferedSink sink() {
    return sink;
  }

  public HashCode hash() {
    checkState(!sink.isOpen());
    return hashSupplier.get();
  }

  @Override
  public void close() throws IOException {
    sink.close();
  }
}
