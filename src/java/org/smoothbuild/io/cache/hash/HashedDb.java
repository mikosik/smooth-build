package org.smoothbuild.io.cache.hash;

import static org.smoothbuild.io.cache.hash.HashCodes.toPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.io.cache.hash.err.NoObjectWithGivenHashError;
import org.smoothbuild.io.cache.hash.err.ReadingHashedObjectFailedError;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import com.google.common.hash.HashCode;

public class HashedDb {
  private final FileSystem dbFileSystem;

  public HashedDb(FileSystem dbFileSystem) {
    this.dbFileSystem = dbFileSystem;
  }

  public boolean contains(HashCode hash) {
    Path path = toPath(hash);
    return dbFileSystem.pathState(path) == PathState.FILE;
  }

  public InputStream openInputStream(HashCode hash) {
    Path path = toPath(hash);
    if (dbFileSystem.pathState(path) == PathState.FILE) {
      return dbFileSystem.openInputStream(path);
    } else {
      throw new NoObjectWithGivenHashError(hash);
    }
  }

  public HashCode store(byte[] bytes) {
    return store(Hash.bytes(bytes), bytes);
  }

  public HashCode store(HashCode hash, byte[] bytes) {
    Path path = toPath(hash);

    if (dbFileSystem.pathState(path) == PathState.FILE) {
      return hash;
    }

    try (OutputStream outputStream = dbFileSystem.openOutputStream(path)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new ReadingHashedObjectFailedError(hash, e);
    }

    return hash;
  }
}
