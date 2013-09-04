package org.smoothbuild.fs.base;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.plugin.Path;

public class SubFileSystem implements FileSystem {
  private final FileSystem fileSystem;
  private final Path root;

  public SubFileSystem(FileSystem fileSystem, Path root) {
    this.fileSystem = fileSystem;
    this.root = root;
  }

  @Override
  public boolean pathExists(Path path) {
    return fileSystem.pathExists(absolutePath(path));
  }

  @Override
  public boolean pathExistsAndIsDirectory(Path path) {
    return fileSystem.pathExistsAndIsDirectory(absolutePath(path));
  }

  @Override
  public boolean pathExistsAndIsFile(Path path) {
    return fileSystem.pathExistsAndIsFile(absolutePath(path));
  }

  @Override
  public Iterable<String> childNames(Path directory) {
    return fileSystem.childNames(absolutePath(directory));
  }

  @Override
  public Iterable<Path> filesFrom(Path directory) {
    return fileSystem.filesFrom(absolutePath(directory));
  }

  @Override
  public void copy(Path sourceFile, Path destinationFile) {
    fileSystem.copy(absolutePath(sourceFile), absolutePath(destinationFile));

  }

  @Override
  public void deleteDirectoryRecursively(Path directory) {
    fileSystem.deleteDirectoryRecursively(absolutePath(directory));
  }

  @Override
  public InputStream createInputStream(Path path) {
    return fileSystem.createInputStream(absolutePath(path));
  }

  @Override
  public OutputStream createOutputStream(Path path) {
    return fileSystem.createOutputStream(absolutePath(path));
  }

  private Path absolutePath(Path path) {
    return root.append(path);
  }

}
