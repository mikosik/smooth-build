package org.smoothbuild.io.cache.hash;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import org.smoothbuild.io.cache.hash.err.CorruptedBoolError;
import org.smoothbuild.io.cache.hash.err.CorruptedEnumValue;
import org.smoothbuild.io.cache.hash.err.IllegalPathInObjectError;
import org.smoothbuild.io.cache.hash.err.ReadingHashedObjectFailedError;
import org.smoothbuild.io.cache.hash.err.TooFewBytesToUnmarshallValue;
import org.smoothbuild.io.cache.hash.err.WritingHashedObjectFailedError;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.primitives.Ints;

public class Unmarshaller implements Closeable {
  private final HashCode hash;
  private final DataInputStream inputStream;

  public Unmarshaller(HashedDb hashedDb, HashCode hash) {
    this.hash = hash;
    this.inputStream = new DataInputStream(hashedDb.openInputStream(hash));
  }

  public List<HashCode> readHashCodeList() {
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
      throw new ErrorMessageException(new IllegalPathInObjectError(hash, e.getMessage()));
    }
  }

  public HashCode readHash() {
    byte[] bytes = readBytes(Hash.size(), "hash");
    return HashCode.fromBytes(bytes);
  }

  public boolean readBool() {
    byte byteValue = readBytes(1, "bool")[0];
    switch (byteValue) {
      case HashedDb.FALSE_AS_BYTE:
        return false;
      case HashedDb.TRUE_AS_BYTE:
        return true;
      default:
        throw new ErrorMessageException(new CorruptedBoolError(byteValue));
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
      throw new ErrorMessageException(new CorruptedEnumValue(enumValues, byteValue));
    }
  }

  private byte[] readBytes(int size, String valueName) {
    try {
      return readBytesImpl(size, valueName);
    } catch (IOException e) {
      throw new ErrorMessageException(new ReadingHashedObjectFailedError(hash, e));
    }
  }

  private byte[] readBytesImpl(int size, String valueName) throws IOException {
    byte[] bytes = new byte[size];
    int read = inputStream.read(bytes);
    if (read < size) {
      throw new ErrorMessageException(new TooFewBytesToUnmarshallValue(hash, valueName, size, read));
    }
    return bytes;
  }

  @Override
  public void close() {
    try {
      inputStream.close();
    } catch (IOException e) {
      throw new ErrorMessageException(new WritingHashedObjectFailedError(hash, e));
    }
  }
}
