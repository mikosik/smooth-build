package org.smoothbuild.db.hashed;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

import com.google.common.hash.HashCode;

public class HashedDb {
  private final FileSystem fileSystem;

  public HashedDb(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public static HashedDb memoryHashedDb() {
    return new HashedDb(new MemoryFileSystem());
  }

  public boolean contains(HashCode hash) {
    Path path = Hash.toPath(hash);
    return fileSystem.pathState(path) == PathState.FILE;
  }

  public Unmarshaller newUnmarshaller(HashCode hash) {
    Path path = Hash.toPath(hash);
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
    return new Marshaller(fileSystem, hash);
  }
}
