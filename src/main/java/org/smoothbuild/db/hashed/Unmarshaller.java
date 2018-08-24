package org.smoothbuild.db.hashed;

import static okio.Okio.buffer;

import java.io.Closeable;
import java.io.IOException;

import com.google.common.hash.HashCode;

import okio.BufferedSource;
import okio.Source;

public class Unmarshaller implements Closeable {
  private final BufferedSource source;

  public Unmarshaller(Source source) {
    this.source = buffer(source);
  }

  public BufferedSource source() {
    return source;
  }

  public HashCode readHash() throws NotEnoughBytesException, IOException {
    return readHash(false);
  }

  public HashCode tryReadHash() throws NotEnoughBytesException, IOException {
    return readHash(true);
  }

  private HashCode readHash(boolean allowNull) throws NotEnoughBytesException, IOException {
    byte[] bytes = readBytes(Hash.size(), "hash", allowNull);
    if (bytes == null && allowNull) {
      return null;
    }
    return HashCode.fromBytes(bytes);
  }

  private byte[] readBytes(int size, String valueName, boolean allowNull)
      throws NotEnoughBytesException, IOException {
    byte[] bytes = new byte[size];
    int read = source.read(bytes);
    if (read < size) {
      if (read == -1 && allowNull) {
        return null;
      } else {
        read = Math.max(0, read);
        throw new NotEnoughBytesException(size, read);
      }
    }
    return bytes;
  }

  @Override
  public void close() throws IOException {
    source.close();
  }
}
