package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.Sink;
import okio.Source;

/**
 * This class is thread-safe.
 */
public class SynchronizedBucket implements FileSystem<Path> {
  private final FileSystem<Path> fileSystem;

  public SynchronizedBucket(FileSystem<Path> fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public synchronized PathState pathState(Path path) throws IOException {
    return fileSystem.pathState(path);
  }

  @Override
  public PathIterator filesRecursively(Path dir) throws IOException {
    return fileSystem.filesRecursively(dir);
  }

  @Override
  public synchronized Iterable<Path> files(Path dir) throws IOException {
    return fileSystem.files(dir);
  }

  @Override
  public synchronized void move(Path source, Path target) throws IOException {
    fileSystem.move(source, target);
  }

  @Override
  public synchronized void delete(Path path) throws IOException {
    fileSystem.delete(path);
  }

  @Override
  public synchronized long size(Path path) throws IOException {
    return fileSystem.size(path);
  }

  @Override
  public synchronized Source source(Path path) throws IOException {
    return fileSystem.source(path);
  }

  @Override
  public synchronized Sink sink(Path path) throws IOException {
    return fileSystem.sink(path);
  }

  @Override
  public synchronized void createLink(Path link, Path target) throws IOException {
    fileSystem.createLink(link, target);
  }

  @Override
  public synchronized void createDir(Path path) throws IOException {
    fileSystem.createDir(path);
  }
}
