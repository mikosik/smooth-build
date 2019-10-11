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

  public HashCode readHash() throws IOException {
    return readHash(false);
  }

  public HashCode tryReadHash() throws IOException {
    return readHash(true);
  }

  private HashCode readHash(boolean allowNull) throws IOException {
    if (allowNull && source.exhausted()) {
      return null;
    }
    return Hash.read(this.source);
  }

  @Override
  public void close() throws IOException {
    source.close();
  }
}
