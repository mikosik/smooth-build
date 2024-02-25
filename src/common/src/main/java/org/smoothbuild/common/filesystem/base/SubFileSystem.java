package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

public class SubFileSystem implements FileSystem {
  private final FileSystem fileSystem;
  private final PathS root;

  public SubFileSystem(FileSystem fileSystem, PathS root) {
    this.fileSystem = fileSystem;
    this.root = root;
  }

  @Override
  public PathState pathState(PathS path) {
    return fileSystem.pathState(fullPath(path));
  }

  @Override
  public Iterable<PathS> files(PathS dir) throws IOException {
    return fileSystem.files(fullPath(dir));
  }

  @Override
  public void move(PathS source, PathS target) throws IOException {
    fileSystem.move(fullPath(source), fullPath(target));
  }

  @Override
  public void delete(PathS path) throws IOException {
    fileSystem.delete(fullPath(path));
  }

  @Override
  public long size(PathS path) throws IOException {
    return fileSystem.size(fullPath(path));
  }

  @Override
  public BufferedSource source(PathS path) throws IOException {
    return fileSystem.source(fullPath(path));
  }

  @Override
  public BufferedSink sink(PathS path) throws IOException {
    return fileSystem.sink(fullPath(path));
  }

  @Override
  public Sink sinkWithoutBuffer(PathS path) throws IOException {
    return fileSystem.sinkWithoutBuffer(fullPath(path));
  }

  @Override
  public void createLink(PathS link, PathS target) throws IOException {
    fileSystem.createLink(fullPath(link), fullPath(target));
  }

  @Override
  public void createDir(PathS path) throws IOException {
    fileSystem.createDir(fullPath(path));
  }

  private PathS fullPath(PathS path) {
    return root.append(path);
  }
}
