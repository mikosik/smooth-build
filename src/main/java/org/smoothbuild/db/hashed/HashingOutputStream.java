package org.smoothbuild.db.hashed;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class HashingOutputStream extends OutputStream {
  private final OutputStream outputStream;
  private final Hasher hasher;
  private HashCode hash;

  public HashingOutputStream(OutputStream outputStream) {
    this.outputStream = outputStream;
    this.hasher = Hash.newHasher();
    this.hash = null;
  }

  @Override
  public void write(int b) throws IOException {
    write(new byte[] { (byte) b });
  }

  @Override
  public void write(byte b[]) throws IOException {
    write(b, 0, b.length);
  }

  @Override
  public void write(byte bytes[], int off, int len) throws IOException {
    hasher.putBytes(bytes, off, len);
    outputStream.write(bytes, off, len);
  }

  public HashCode hash() {
    return hash;
  }

  @Override
  public void close() throws IOException {
    if (hash == null) {
      hash = hasher.hash();
    }
    outputStream.close();
  }
}
