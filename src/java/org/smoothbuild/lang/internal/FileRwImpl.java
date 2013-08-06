package org.smoothbuild.lang.internal;

import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.type.FileRw;
import org.smoothbuild.lang.type.Path;

public class FileRwImpl extends FileRoImpl implements FileRw {
  public FileRwImpl(FileSystem fileSystem, Path root, Path path) {
    super(fileSystem, root, path);
  }

  @Override
  public OutputStream createOutputStream() {
    return fileSystem().createOutputStream(fullPath());
  }
}
