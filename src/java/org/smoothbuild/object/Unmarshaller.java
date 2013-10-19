package org.smoothbuild.object;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.object.HashedDb.STRING_CHARSET;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.hash.Hash;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.err.IllegalPathInObjectError;
import org.smoothbuild.object.err.TooFewBytesToUnmarshallValue;

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
    try {
      return readPathImpl();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private Path readPathImpl() throws IOException {
    int size = inputStream.readInt();
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
      throw new ErrorMessageException(new IllegalPathInObjectError(e.getMessage()));
    }
  }

  public HashCode readHash() {
    try {
      return readHashImpl();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private HashCode readHashImpl() throws IOException {
    byte[] bytes = readBytes(Hash.size(), "hash");
    return HashCode.fromBytes(bytes);
  }

  public int readInt() {
    try {
      return readIntImpl();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private int readIntImpl() throws IOException {
    byte[] bytes = readBytes(4, "int");
    return Ints.fromByteArray(bytes);
  }

  private byte[] readBytes(int size, String valueName) throws IOException {
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
      throw new FileSystemException(e);
    }
  }
}
