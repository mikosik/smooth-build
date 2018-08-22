package org.smoothbuild.db.hashed;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.hash.HashCode;

public class Unmarshaller extends InputStream {
  private final HashCode hash;
  private final InputStream inputStream;

  public Unmarshaller(HashCode hash, InputStream inputStream) {
    this.hash = hash;
    this.inputStream = inputStream;
  }

  public HashCode readHash() {
    return readHash(false);
  }

  public HashCode tryReadHash() {
    return readHash(true);
  }

  @Override
  public int read() throws IOException {
    return inputStream.read();
  }

  @Override
  public int read(byte b[], int off, int len) throws IOException {
    return inputStream.read(b, off, len);
  }

  @Override
  public int read(byte b[]) throws IOException {
    return inputStream.read(b);
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
      int read = inputStream.read(bytes);
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
  public void close() {
    try {
      inputStream.close();
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while reading " + hash + " object.");
    }
  }
}
