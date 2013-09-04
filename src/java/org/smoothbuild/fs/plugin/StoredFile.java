package org.smoothbuild.fs.plugin;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.MutableFile;
import org.smoothbuild.plugin.Path;

public class StoredFile implements MutableFile {
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

  public Path fullPath() {
    return fileSystem.root().append(path);
  }

  @Override
  public InputStream createInputStream() {
    return fileSystem.createInputStream(path);
  }

  @Override
  public OutputStream createOutputStream() {
    return fileSystem.createOutputStream(path);
  }
}
