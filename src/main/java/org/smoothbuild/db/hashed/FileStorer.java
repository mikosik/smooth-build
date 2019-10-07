package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.Hash.toPath;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.io.IOException;
import java.util.function.Supplier;

import org.smoothbuild.io.fs.base.AssertPath;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import com.google.common.hash.HashCode;

public class FileStorer {
  private final FileSystem fileSystem;
  private final Path hashedDbRoot;
  private final Path sourceFile;
  private final Supplier<HashCode> hashSupplier;

  public FileStorer(FileSystem fileSystem, Path hashedDbRoot, Path sourceFile,
      Supplier<HashCode> hashSupplier) {
    this.fileSystem = fileSystem;
    this.hashedDbRoot = hashedDbRoot;
    this.sourceFile = sourceFile;
    this.hashSupplier = hashSupplier;
  }

  public void store() throws IOException {
    Path path = hashedDbRoot.append(toPath(hashSupplier.get()));
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case NOTHING:
        fileSystem.move(sourceFile, path);
      case FILE:
        // nothing to do, we already stored data with such hash so its content must be equal
        return;
      case DIR:
        throw new CorruptedHashedDbException(
            "Corrupted HashedDb. Cannot store data at " + path + " as it is a directory.");
      default:
        throw AssertPath.newUnknownPathState(pathState);
    }
  }
}
