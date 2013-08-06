package org.smoothbuild.lang.internal;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.FilesRo;
import org.smoothbuild.lang.type.Path;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class FilesRoImpl implements FilesRo {
  private final PathToFileRoFunction pathToFileRo = new PathToFileRoFunction();
  private final FileSystem fileSystem;
  private final Path root;

  public FilesRoImpl(FileSystem fileSystem, Path root) {
    this.fileSystem = fileSystem;
    this.root = root;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  public Path root() {
    return root;
  }

  @Override
  public FileRo fileRo(Path path) {
    return new FileRoImpl(fileSystem, root, path);
  }

  @Override
  public Iterable<FileRo> asIterable() {
    Iterable<Path> filesIterable = fileSystem.filesFrom(root);
    return FluentIterable.from(filesIterable).transform(pathToFileRo);
  }

  private class PathToFileRoFunction implements Function<Path, FileRo> {
    public FileRo apply(Path path) {
      return new FileRoImpl(fileSystem, root, path);
    }
  }
}
