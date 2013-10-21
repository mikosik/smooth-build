package org.smoothbuild.type.impl;

import static org.smoothbuild.object.HashedDb.STRING_CHARSET;

import java.io.IOException;
import java.io.InputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.hash.Hash;
import org.smoothbuild.type.api.File;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class StoredFile implements File {
  private final FileSystem fileSystem;
  private final Path path;

  public StoredFile(FileSystem fileSystem, Path path) {
    this.fileSystem = fileSystem;
    this.path = path;
  }

  @Override
  public Path path() {
    return path;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public InputStream openInputStream() {
    return fileSystem.openInputStream(path);
  }

  @Override
  public String toString() {
    return "StoredFile(" + path + ")";
  }

  @Override
  public HashCode hash() {
    try (InputStream inputStream = openInputStream()) {
      HashCode contentHash = Hash.bytes(ByteStreams.toByteArray((inputStream)));

      ByteArrayDataOutput dataOutput = ByteStreams.newDataOutput(256);

      dataOutput.write(contentHash.asBytes());

      byte[] pathBytes = path.value().getBytes(STRING_CHARSET);
      dataOutput.writeInt(pathBytes.length);
      dataOutput.write(pathBytes);

      return Hash.bytes(dataOutput.toByteArray());
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
