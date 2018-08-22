package org.smoothbuild.db.hashed;

import java.io.OutputStream;
import java.util.function.Supplier;

import com.google.common.hash.HashCode;

public class Marshaller extends OutputStream {
  private final StoringOutputStream outputStream;
  private final Supplier<HashCode> hashSupplier;

  public Marshaller(StoringOutputStream outputStream, Supplier<HashCode> hashSupplier) {
    this.outputStream = outputStream;
    this.hashSupplier = hashSupplier;
  }

  public Marshaller writeHash(HashCode hash) {
    write(hash.asBytes());
    return this;
  }

  @Override
  public void write(int b) {
    outputStream.write(b);
  }

  @Override
  public void write(byte b[]) {
    outputStream.write(b);
  }

  @Override
  public void write(byte bytes[], int off, int len) {
    outputStream.write(bytes, off, len);
  }

  @Override
  public void close() {
    outputStream.close();
  }

  public HashCode hash() {
    return hashSupplier.get();
  }
}
