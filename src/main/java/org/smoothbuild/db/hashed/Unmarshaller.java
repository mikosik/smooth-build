package org.smoothbuild.db.hashed;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.db.hashed.Constants.FALSE_AS_BYTE;
import static org.smoothbuild.db.hashed.Constants.TRUE_AS_BYTE;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.smoothbuild.db.hashed.err.CorruptedBoolException;
import org.smoothbuild.db.hashed.err.CorruptedEnumException;
import org.smoothbuild.db.hashed.err.IllegalPathInObjectException;
import org.smoothbuild.db.hashed.err.ReadingHashedObjectFailedException;
import org.smoothbuild.db.hashed.err.TooFewBytesToUnmarshallValueException;
import org.smoothbuild.io.fs.base.Path;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.primitives.Ints;

public class Unmarshaller implements Closeable {
  private final HashCode hash;
  private final InputStream inputStream;

  public Unmarshaller(HashedDb hashedDb, HashCode hash) {
    this.hash = hash;
    this.inputStream = hashedDb.openInputStream(hash);
  }

  public List<HashCode> readHashList() {
    ImmutableList.Builder<HashCode> builder = ImmutableList.builder();
    int size = readInt();
    for (int i = 0; i < size; i++) {
      builder.add(readHash());
    }
    return builder.build();
  }

  public Path readPath() {
    int size = readInt();
    byte[] bytes = readBytes(size, "path");

    /*
     * This method always replaces malformed-input and unmappable-character
     * sequences with this charset's default replacement string. The {@link
     * java.nio.charset.CharsetDecoder} class should be used when more control
     * over the decoding process is required.
     */
    String value = new String(bytes, CHARSET);

    return toPathSafely(value);
  }

  private Path toPathSafely(String value) {
    try {
      return path(value);
    } catch (IllegalArgumentException e) {
      throw new IllegalPathInObjectException(hash, e.getMessage());
    }
  }

  public HashCode readHash() {
    byte[] bytes = readBytes(Hash.size(), "hash");
    return HashCode.fromBytes(bytes);
  }

  public boolean readBool() {
    byte byteValue = readBytes(1, "bool")[0];
    switch (byteValue) {
      case FALSE_AS_BYTE:
        return false;
      case TRUE_AS_BYTE:
        return true;
      default:
        throw new CorruptedBoolException(byteValue);
    }
  }

  public byte readByte() {
    byte[] bytes = readBytes(1, "byte");
    return bytes[0];
  }

  public int readInt() {
    byte[] bytes = readBytes(4, "int");
    return Ints.fromByteArray(bytes);
  }

  public <T> T readEnum(EnumValues<T> enumValues) {
    byte byteValue = readByte();
    if (enumValues.isValidByte(byteValue)) {
      return enumValues.byteToValue(byteValue);
    } else {
      throw new CorruptedEnumException(enumValues, byteValue);
    }
  }

  private byte[] readBytes(int size, String valueName) {
    try {
      return readBytesImpl(size, valueName);
    } catch (IOException e) {
      throw new ReadingHashedObjectFailedException(hash, e);
    }
  }

  private byte[] readBytesImpl(int size, String valueName) throws IOException {
    byte[] bytes = new byte[size];
    int read = inputStream.read(bytes);
    if (read < size) {
      throw new TooFewBytesToUnmarshallValueException(hash, valueName, size, read);
    }
    return bytes;
  }

  @Override
  public void close() {
    try {
      inputStream.close();
    } catch (IOException e) {
      throw new ReadingHashedObjectFailedException(hash, e);
    }
  }
}
