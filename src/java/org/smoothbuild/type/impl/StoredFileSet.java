package org.smoothbuild.type.impl;

import static org.smoothbuild.fs.base.PathState.FILE;

import java.util.Iterator;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * Set of files on given fileSystem.
 */
public class StoredFileSet implements FileSet {
  private final PathToFileConverter pathToFileConverter = new PathToFileConverter();
  private final FileSystem fileSystem;

  public StoredFileSet(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public boolean contains(Path path) {
    return fileSystem.pathState(path) == FILE;
  }

  public FileSystem fileSystem() {
    return fileSystem;
  }

  @Override
  public Iterator<File> iterator() {
    Iterable<Path> filesIterable = fileSystem.filesFrom(Path.rootPath());
    return Iterators.transform(filesIterable.iterator(), pathToFileConverter);
  }

  private class PathToFileConverter implements Function<Path, File> {
    @Override
    public File apply(Path path) {
      return new StoredFile(fileSystem, path);
    }
  }
}
