package org.smoothbuild.lang.internal;

import java.io.InputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.Path;

public class FileRoImpl implements FileRo {
  private final FileSystem fileSystem;
  private final Path path;
  private final Path fullPath;

  public FileRoImpl(FileSystem fileSystem, Path rootDir, Path path) {
    this.fileSystem = fileSystem;
    this.path = path;
    this.fullPath = rootDir.append(path);
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public Path path() {
    return path;
  }

  public Path fullPath() {
    return fullPath;
  }

  @Override
  public InputStream createInputStream() {
    return fileSystem.createInputStream(fullPath.value());
  }
}
