package org.smoothbuild.common.filesystem.space;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import okio.BufferedSource;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathState;

/**
 * This class is thread-safe.
 */
@Singleton
public class FileResolver {
  private final Map<Space, FileSystem> fileSystems;

  @Inject
  public FileResolver(Map<Space, FileSystem> fileSystems) {
    this.fileSystems = fileSystems;
  }

  public String contentOf(FullPath fullPath, Charset charset) throws IOException {
    try (BufferedSource source = source(fullPath)) {
      return source.readString(charset);
    }
  }

  public BufferedSource source(FullPath fullPath) throws IOException {
    return fileSystemFor(fullPath).source(fullPath.path());
  }

  public PathState pathState(FullPath fullPath) {
    return fileSystemFor(fullPath).pathState(fullPath.path());
  }

  private FileSystem fileSystemFor(FullPath fullPath) {
    return fileSystemFor(fullPath.space());
  }

  private FileSystem fileSystemFor(Space space) {
    FileSystem fileSystem = fileSystems.get(space);
    if (fileSystem == null) {
      throw new IllegalArgumentException("Unknown space " + space + ".");
    }
    return fileSystem;
  }
}
