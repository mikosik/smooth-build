package org.smoothbuild.db.hashed;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import com.google.common.hash.HashCode;

public class HashedDb {
  private final FileSystem fileSystem;
  private final Path rootPath;

  public HashedDb(FileSystem fileSystem, Path rootPath) {
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
  }

  public boolean contains(HashCode hash) {
    Path path = toPath(hash);
    return fileSystem.pathState(path) == PathState.FILE;
  }

  public Unmarshaller newUnmarshaller(HashCode hash) {
    Path path = toPath(hash);
    if (fileSystem.pathState(path) == PathState.FILE) {
      return new Unmarshaller(hash, fileSystem.openInputStream(path));
    } else {
      throw new HashedDbException("Could not find " + hash + " object.");
    }
  }

  public Marshaller newMarshaller() {
    return newMarshaller(null);
  }

  public Marshaller newMarshaller(HashCode hash) {
    return new Marshaller(fileSystem, rootPath, hash);
  }

  private Path toPath(HashCode hash) {
    return rootPath.append(Hash.toPath(hash));
  }
}
