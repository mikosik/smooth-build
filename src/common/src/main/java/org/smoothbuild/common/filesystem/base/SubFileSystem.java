package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

public class SubFileSystem implements FileSystem {
  private final FileSystem fileSystem;
  private final Path root;

  public SubFileSystem(FileSystem fileSystem, Path root) {
    this.fileSystem = fileSystem;
    this.root = root;
  }

  @Override
  public PathState pathState(Path path) {
    return fileSystem.pathState(fullPath(path));
  }

  @Override
  public Iterable<Path> files(Path dir) throws IOException {
    return fileSystem.files(fullPath(dir));
  }

  @Override
  public void move(Path source, Path target) throws IOException {
    fileSystem.move(fullPath(source), fullPath(target));
  }

  @Override
  public void delete(Path path) throws IOException {
    fileSystem.delete(fullPath(path));
  }

  @Override
  public long size(Path path) throws IOException {
    return fileSystem.size(fullPath(path));
  }

  @Override
  public BufferedSource source(Path path) throws IOException {
    return fileSystem.source(fullPath(path));
  }

  @Override
  public BufferedSink sink(Path path) throws IOException {
    return fileSystem.sink(fullPath(path));
  }

  @Override
  public Sink sinkWithoutBuffer(Path path) throws IOException {
    return fileSystem.sinkWithoutBuffer(fullPath(path));
  }

  @Override
  public void createLink(Path link, Path target) throws IOException {
    fileSystem.createLink(fullPath(link), fullPath(target));
  }

  @Override
  public void createDir(Path path) throws IOException {
    fileSystem.createDir(fullPath(path));
  }

  private Path fullPath(Path path) {
    return root.append(path);
  }
}
