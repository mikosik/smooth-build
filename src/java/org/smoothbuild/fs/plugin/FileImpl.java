package org.smoothbuild.fs.plugin;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;

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

  @Override
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
