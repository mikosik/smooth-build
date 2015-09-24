package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.HashCodes.toPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.db.hashed.err.NoObjectWithGivenHashError;
import org.smoothbuild.db.hashed.err.WritingHashedObjectFailedError;
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

  public HashCode write(byte[] bytes) {
    return write(Hash.bytes(bytes), bytes);
  }

  public HashCode write(HashCode hash, byte[] bytes) {
    Path path = toPath(hash);

    if (dbFileSystem.pathState(path) == PathState.FILE) {
      return hash;
    }

    try (OutputStream outputStream = dbFileSystem.openOutputStream(path)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new WritingHashedObjectFailedError(hash, e);
    }

    return hash;
  }
}
