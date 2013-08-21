package org.smoothbuild.fs.plugin;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Files;
import org.smoothbuild.plugin.Path;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class FilesImpl implements Files {
  private final PathToFileConverter pathToFileConverter = new PathToFileConverter();
  private final FileSystem fileSystem;
  private final Path root;

  public FilesImpl(FileSystem fileSystem, Path root) {
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
  public File file(Path path) {
    return new FileImpl(fileSystem, root, path);
  }

  @Override
  public Iterable<File> asIterable() {
    Iterable<Path> filesIterable = fileSystem.filesFrom(root);
    return FluentIterable.from(filesIterable).transform(pathToFileConverter);
  }

  @Override
  public FileImpl createFile(Path path) {
    return new FileImpl(fileSystem(), root(), path);
  }

  private class PathToFileConverter implements Function<Path, File> {
    public File apply(Path path) {
      return new FileImpl(fileSystem, root, path);
    }
  }
}
