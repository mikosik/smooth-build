package org.smoothbuild.lang.internal;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Path;

public class FileImpl implements File {
  private final FileSystem fileSystem;
  private final Path path;
  private final Path fullPath;

  public FileImpl(FileSystem fileSystem, Path rootDir, Path path) {
    this.fileSystem = fileSystem;
    this.path = path;
    this.fullPath = rootDir.append(path);
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
    return fileSystem.createInputStream(fullPath);
  }

  @Override
  public OutputStream createOutputStream() {
    return fileSystem.createOutputStream(fullPath);
  }
}
