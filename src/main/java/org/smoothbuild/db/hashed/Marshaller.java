package org.smoothbuild.db.hashed;

import java.io.IOException;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Marshaller extends OutputStream {
  private final FileSystem fileSystem;
  private final Path rootPath;
  private final HashCode hash;
  private final ByteArrayDataOutput dataOutput;

  public Marshaller(FileSystem fileSystem, Path rootPath, HashCode hash) {
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.hash = hash;
    this.dataOutput = ByteStreams.newDataOutput(256);
  }

  public void writeHash(HashCode hash) {
    dataOutput.write(hash.asBytes());
  }

  public void writeInt(int intValue) {
    dataOutput.writeInt(intValue);
  }

  @Override
  public void write(int b) throws IOException {
    dataOutput.write(b);
  }

  @Override
  public void write(byte b[]) throws IOException {
    dataOutput.write(b, 0, b.length);
  }

  @Override
  public void write(byte b[], int off, int len) {
    dataOutput.write(b, off, len);
  }

  public HashCode closeMarshaller() {
    byte[] bytes = dataOutput.toByteArray();
    if (hash == null) {
      return write(Hash.bytes(bytes), bytes);
    } else {
      return write(hash, bytes);
    }
  }

  public HashCode write(HashCode hash, byte[] bytes) {
    Path path = rootPath.append(Hash.toPath(hash));
    if (fileSystem.pathState(path) == PathState.FILE) {
      return hash;
    }
    try (OutputStream outputStream = fileSystem.openOutputStream(path)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while writing " + hash + " object.");
    }
    return hash;
  }
}
