package org.smoothbuild.fs.base;

import java.io.IOException;
import java.nio.file.Path;

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
  public synchronized Path rootDirJPath() {
    return fileSystem.rootDirJPath();
  }

  @Override
  public synchronized PathState pathState(PathS path) {
    return fileSystem.pathState(path);
  }

  @Override
  public synchronized Iterable<PathS> files(PathS dir) throws IOException {
    return fileSystem.files(dir);
  }

  @Override
  public synchronized void move(PathS source, PathS target) throws IOException {
    fileSystem.move(source, target);
  }

  @Override
  public synchronized void delete(PathS path) throws IOException {
    fileSystem.delete(path);
  }

  @Override
  public synchronized long size(PathS path) throws IOException {
    return fileSystem.size(path);
  }

  @Override
  public synchronized BufferedSource source(PathS path) throws IOException {
    return fileSystem.source(path);
  }

  @Override
  public synchronized BufferedSink sink(PathS path) throws IOException {
    return fileSystem.sink(path);
  }

  @Override
  public synchronized Sink sinkWithoutBuffer(PathS path) throws IOException {
    return fileSystem.sinkWithoutBuffer(path);
  }

  @Override
  public synchronized void createLink(PathS link, PathS target) throws IOException {
    fileSystem.createLink(link, target);
  }

  @Override
  public synchronized void createDir(PathS path) throws IOException {
    fileSystem.createDir(path);
  }
}
