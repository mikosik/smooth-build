package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

/**
 * This class is thread-safe.
 */
public class SynchronizedFileSystem implements FileSystem {
  private final FileSystem fileSystem;

  public SynchronizedFileSystem(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public synchronized PathState pathState(Path path) {
    return fileSystem.pathState(path);
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
  public synchronized BufferedSource source(Path path) throws IOException {
    return fileSystem.source(path);
  }

  @Override
  public synchronized BufferedSink sink(Path path) throws IOException {
    return fileSystem.sink(path);
  }

  @Override
  public synchronized Sink sinkWithoutBuffer(Path path) throws IOException {
    return fileSystem.sinkWithoutBuffer(path);
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
