package org.smoothbuild.type.impl;

import java.io.InputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;

public class StoredFile implements File {
  private final FileSystem fileSystem;
  private final Path path;

  public StoredFile(FileSystem fileSystem, Path path) {
    this.fileSystem = fileSystem;
    this.path = path;
  }

  @Override
  public Path path() {
    return path;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public InputStream openInputStream() {
    return fileSystem.openInputStream(path);
  }

  @Override
  public String toString() {
    return "StoredFile(" + path + ")";
  }
}
