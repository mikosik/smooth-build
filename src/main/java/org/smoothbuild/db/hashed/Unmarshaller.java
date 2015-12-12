package org.smoothbuild.db.hashed;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.hash.HashCode;
import com.google.common.primitives.Ints;

public class Unmarshaller implements Closeable {
  private final HashCode hash;
  private final InputStream inputStream;

  public Unmarshaller(HashedDb hashedDb, HashCode hash) {
    this.hash = hash;
    this.inputStream = hashedDb.openInputStream(hash);
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

  public int readInt() {
    byte[] bytes = readBytes(4, "int", false);
    return Ints.fromByteArray(bytes);
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
