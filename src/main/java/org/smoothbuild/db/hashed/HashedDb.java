package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.HashCodes.toPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashException;
import org.smoothbuild.db.hashed.err.WritingHashedObjectFailedException;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import com.google.common.hash.HashCode;

public class HashedDb {
  private final FileSystem fileSystem;

  public HashedDb(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public boolean contains(HashCode hash) {
    Path path = toPath(hash);
    return fileSystem.pathState(path) == PathState.FILE;
  }

  public InputStream openInputStream(HashCode hash) {
    Path path = toPath(hash);
    if (fileSystem.pathState(path) == PathState.FILE) {
      return fileSystem.openInputStream(path);
    } else {
      throw new NoObjectWithGivenHashException(hash);
    }
  }

  public HashCode write(byte[] bytes) {
    return write(Hash.bytes(bytes), bytes);
  }

  public HashCode write(HashCode hash, byte[] bytes) {
    Path path = toPath(hash);

    if (fileSystem.pathState(path) == PathState.FILE) {
      return hash;
    }

    try (OutputStream outputStream = fileSystem.openOutputStream(path)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new WritingHashedObjectFailedException(hash, e);
    }

    return hash;
  }
}
