package org.smoothbuild.fs.plugin;

import java.io.InputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;

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

  public Path fullPath() {
    return fileSystem.root().append(path);
  }

  @Override
  public InputStream createInputStream() {
    return fileSystem.createInputStream(path);
  }
}
