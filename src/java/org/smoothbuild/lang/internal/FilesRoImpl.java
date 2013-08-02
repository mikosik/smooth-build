package org.smoothbuild.lang.internal;

import static org.smoothbuild.lang.type.Path.path;

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
    Iterable<String> filesIterable = fileSystem.filesFrom(root.value());
    return FluentIterable.from(filesIterable).transform(pathToFileRo);
  }

  private class PathToFileRoFunction implements Function<String, FileRo> {
    public FileRo apply(String path) {
      return new FileRoImpl(fileSystem, root, path(path));
    }
  }
}
