package org.smoothbuild.fs.base;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.type.api.Path;

public class SubFileSystem implements FileSystem {
  private final FileSystem fileSystem;
  private final Path root;

  @Override
  public Path root() {
    return fileSystem.root().append(root);
  }

  public SubFileSystem(FileSystem fileSystem, Path root) {
    this.fileSystem = fileSystem;
    this.root = root;
  }

  @Override
  public PathKind pathKind(Path path) {
    return fileSystem.pathKind(absolutePath(path));
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
  public InputStream openInputStream(Path path) {
    return fileSystem.openInputStream(absolutePath(path));
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    return fileSystem.openOutputStream(absolutePath(path));
  }

  private Path absolutePath(Path path) {
    return root.append(path);
  }

}
