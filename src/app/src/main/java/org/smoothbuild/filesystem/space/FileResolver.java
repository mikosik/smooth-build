package org.smoothbuild.filesystem.space;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.BufferedSource;
import org.smoothbuild.SmoothConstants;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathState;
import org.smoothbuild.vm.bytecode.hashed.Hash;

/**
 * This class is thread-safe.
 */
@Singleton
public class FileResolver {
  private final Map<Space, FileSystem> fileSystems;
  private final Map<FilePath, Hash> cache;

  @Inject
  public FileResolver(Map<Space, FileSystem> fileSystems) {
    this.fileSystems = fileSystems;
    this.cache = new HashMap<>();
  }

  public synchronized Hash hashOf(FilePath filePath) throws IOException {
    Hash hash = cache.get(filePath);
    if (hash == null) {
      hash = Hash.of(source(filePath));
      cache.put(filePath, hash);
    }
    return hash;
  }

  public synchronized String readFileContentAndCacheHash(FilePath filePath) throws IOException {
    String content = readFileContent(filePath);
    if (!cache.containsKey(filePath)) {
      cache.put(filePath, Hash.of(content));
    }
    return content;
  }

  private String readFileContent(FilePath filePath) throws IOException {
    try (BufferedSource source = source(filePath)) {
      return source.readString(SmoothConstants.CHARSET);
    }
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
