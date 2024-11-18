package org.smoothbuild.common.filesystem.base;

import java.io.IOException;
import okio.Sink;
import okio.Source;

public class FileSystemPart implements FileSystem<Path> {
  private final FileSystem<FullPath> fileSystem;
  private final FullPath root;

  private FileSystemPart(FileSystem<FullPath> fileSystem, FullPath root) {
    this.fileSystem = fileSystem;
    this.root = root;
  }

  public static FileSystemPart fileSystemPart(FileSystem<FullPath> fileSystem, FullPath root) {
    return new FileSystemPart(fileSystem, root);
  }

  @Override
  public PathState pathState(Path path) throws IOException {
    return fileSystem.pathState(fullPath(path));
  }

  @Override
  public PathIterator filesRecursively(Path dir) throws IOException {
    return fileSystem.filesRecursively(fullPath(dir));
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
  public Source source(Path path) throws IOException {
    return fileSystem.source(fullPath(path));
  }

  @Override
  public Sink sink(Path path) throws IOException {
    return fileSystem.sink(fullPath(path));
  }

  @Override
  public void createLink(Path link, Path target) throws IOException {
    fileSystem.createLink(fullPath(link), fullPath(target));
  }

  @Override
  public void createDir(Path path) throws IOException {
    fileSystem.createDir(fullPath(path));
  }

  private FullPath fullPath(Path path) {
    return root.append(path);
  }
}
