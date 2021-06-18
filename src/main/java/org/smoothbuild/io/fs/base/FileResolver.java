package org.smoothbuild.io.fs.base;

import java.io.IOException;

import javax.inject.Inject;

import com.google.common.collect.ImmutableMap;

import okio.BufferedSource;

public class FileResolver {
  private final ImmutableMap<Space, FileSystem> fileSystems;

  @Inject
  public FileResolver(ImmutableMap<Space, FileSystem> fileSystems) {
    this.fileSystems = fileSystems;
  }

  public BufferedSource source(FilePath filePath) throws IOException {
    return fileSystemFor(filePath).source(filePath.path());
  }

  public PathState pathState(FilePath filePath) {
    return fileSystemFor(filePath).pathState(filePath.path());
  }

  private FileSystem fileSystemFor(FilePath filePath) {
    return fileSystemFor(filePath.space());
  }

  private FileSystem fileSystemFor(Space space) {
    FileSystem fileSystem = fileSystems.get(space);
    if (fileSystem == null) {
      throw new IllegalArgumentException("Unknown space " + space + ".");
    }
    return fileSystem;
  }
}
