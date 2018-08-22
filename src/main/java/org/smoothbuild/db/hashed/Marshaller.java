package org.smoothbuild.db.hashed;

import static okio.Okio.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Supplier;

import com.google.common.hash.HashCode;

import okio.BufferedSink;
import okio.Sink;

public class Marshaller extends OutputStream {
  private final BufferedSink bufferedSink;
  private final OutputStream outputStream;
  private final Supplier<HashCode> hashSupplier;

  public Marshaller(Sink sink, Supplier<HashCode> hashSupplier) {
    this.bufferedSink = buffer(sink);
    this.outputStream = bufferedSink.outputStream();
    this.hashSupplier = hashSupplier;
  }

  public Marshaller writeHash(HashCode hash) {
    write(hash.asBytes());
    return this;
  }

  @Override
  public void write(int b) {
    try {
      outputStream.write(b);
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
  }

  @Override
  public void write(byte b[]) {
    try {
      outputStream.write(b);
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
  }

  @Override
  public void write(byte bytes[], int off, int len) {
    try {
      outputStream.write(bytes, off, len);
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
  }

  @Override
  public void close() {
    try {
      outputStream.close();
    } catch (IOException e) {
      rethrowAsHashedDbException(e);
    }
  }

  public HashCode hash() {
    return hashSupplier.get();
  }

  private void rethrowAsHashedDbException(Throwable e) {
    throw new HashedDbException("IO error occurred while writing object.", e);
  }
}
