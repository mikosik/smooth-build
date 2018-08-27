package org.smoothbuild.db.hashed;

import static okio.Okio.buffer;

import java.io.Closeable;
import java.io.EOFException;
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

  public HashCode readHash() throws EOFException, IOException {
    return readHash(false);
  }

  public HashCode tryReadHash() throws EOFException, IOException {
    return readHash(true);
  }

  private HashCode readHash(boolean allowNull) throws EOFException, IOException {
    if (allowNull && source.exhausted()) {
      return null;
    }
    return HashCode.fromBytes(source.readByteArray(Hash.size()));
  }

  @Override
  public void close() throws IOException {
    source.close();
  }
}
