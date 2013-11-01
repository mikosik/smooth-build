package org.smoothbuild.db;

import static org.smoothbuild.db.HashedDb.STRING_CHARSET;
import static org.smoothbuild.fs.base.Path.path;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;

import org.smoothbuild.db.err.IllegalPathInObjectError;
import org.smoothbuild.db.err.ReadingHashedObjectFailedError;
import org.smoothbuild.db.err.TooFewBytesToUnmarshallValue;
import org.smoothbuild.db.err.WritingHashedObjectFailedError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.hash.HashCode;
import com.google.common.primitives.Ints;

public class Unmarshaller implements Closeable {
  private final HashCode hash;
  private final DataInputStream inputStream;

  public Unmarshaller(HashedDb hashedDb, HashCode hash) {
    this.hash = hash;
    this.inputStream = new DataInputStream(hashedDb.openInputStream(hash));
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
    String value = new String(bytes, STRING_CHARSET);

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

  public byte readByte() {
    byte[] bytes = readBytes(1, "byte");
    return bytes[0];
  }

  public int readInt() {
    byte[] bytes = readBytes(4, "int");
    return Ints.fromByteArray(bytes);
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
