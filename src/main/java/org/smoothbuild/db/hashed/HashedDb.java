package org.smoothbuild.db.hashed;

import static org.smoothbuild.io.fs.base.AssertPath.newUnknownPathState;

import java.io.IOException;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.util.TempManager;

import okio.BufferedSource;

public class HashedDb {
  private final FileSystem fileSystem;
  private final Path rootPath;
  private final TempManager tempManager;

  public HashedDb(FileSystem fileSystem, Path rootPath, TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.tempManager = tempManager;
  }

  public boolean contains(Hash hash) throws CorruptedHashedDbException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case FILE:
        return true;
      case DIR:
        throw new CorruptedHashedDbException(
            "Corrupted HashedDb. " + path + " is a directory not a data file.");
      case NOTHING:
        return false;
      default:
        throw newUnknownPathState(pathState);
    }
  }

  public BufferedSource source(Hash hash) throws IOException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case FILE:
        return fileSystem.source(path);
      case DIR:
        throw new CorruptedHashedDbException(
            "Corrupted HashedDb. " + path + " is a directory not a data file.");
      case NOTHING:
        throw new NoSuchDataException(hash);
      default:
        throw newUnknownPathState(pathState);
    }
  }

  public HashingBufferedSink sink() throws IOException {
    return new HashingBufferedSink(fileSystem, tempManager.tempPath(), rootPath);
  }

  private Path toPath(Hash hash) {
    return rootPath.append(Hash.toPath(hash));
  }
}
