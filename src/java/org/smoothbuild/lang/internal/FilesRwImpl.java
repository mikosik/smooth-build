package org.smoothbuild.lang.internal;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.type.FilesRw;
import org.smoothbuild.lang.type.Path;

public class FilesRwImpl extends FilesRoImpl implements FilesRw {
  public FilesRwImpl(FileSystem fileSystem, Path root) {
    super(fileSystem, root);
  }

  @Override
  public FileRwImpl createFileRw(Path path) {
    return new FileRwImpl(fileSystem(), root(), path);
  }
}
