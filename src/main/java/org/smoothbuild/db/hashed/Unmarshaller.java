package org.smoothbuild.db.hashed;

import static okio.Okio.buffer;

import java.io.Closeable;
import java.io.IOException;

import com.google.common.hash.HashCode;

import okio.BufferedSource;
import okio.Source;

public class Unmarshaller implements Closeable {
  private final HashCode hash;
  private final BufferedSource source;

  public Unmarshaller(HashCode hash, Source source) {
    this.source = buffer(source);
    this.hash = hash;
  }

  public BufferedSource source() {
    return source;
  }

  public HashCode readHash() {
    return readHash(false);
  }

  public HashCode tryReadHash() {
    return readHash(true);
  }

  private HashCode readHash(boolean allowNull) {
    byte[] bytes = readBytes(Hash.size(), "hash", allowNull);
    if (bytes == null && allowNull) {
      return null;
    }
    return HashCode.fromBytes(bytes);
  }

  private byte[] readBytes(int size, String valueName, boolean allowNull) {
    try {
      byte[] bytes = new byte[size];
      int read = source.read(bytes);
      if (read < size) {
        if (read == -1 && allowNull) {
          return null;
        } else {
          read = Math.max(0, read);
          throw new HashedDbException("Corrupted " + hash + " object. Value " + valueName
              + " has expected size = " + size + " but only " + read + " is available.");
        }
      }
      return bytes;
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while reading " + hash + " object.");
    }
  }

  @Override
  public void close() throws IOException {
    source.close();
  }
}
