package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.Hash.toPath;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.io.IOException;
import java.util.function.Supplier;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;

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
    if (fileSystem.pathState(path) == NOTHING) {
      fileSystem.move(sourceFile, path);
    }
  }
}
