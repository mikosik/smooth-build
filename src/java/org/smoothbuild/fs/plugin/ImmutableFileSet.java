package org.smoothbuild.fs.plugin;

import static org.smoothbuild.fs.plugin.ImmutableFile.immutableFile;

import java.util.Iterator;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Path;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public class ImmutableFileSet implements FileSet {
  private final FileSet fileSet;

  public static FileSet immutableFiles(FileSet fileSet) {
    if (fileSet instanceof ImmutableFileSet) {
      return fileSet;
    } else {
      return new ImmutableFileSet(fileSet);
    }
  }

  public ImmutableFileSet(FileSet fileSet) {
    this.fileSet = fileSet;
  }

  @Override
  public File file(Path path) {
    return immutableFile(fileSet.file(path));
  }

  @Override
  public Iterator<File> iterator() {
    Function<File, File> transformFunction = new Function<File, File>() {
      public File apply(File file) {
        return immutableFile(file);
      }
    };
    return Iterators.transform(fileSet.iterator(), transformFunction);
  }

  @Override
  public File createFile(Path path) {
    throw new UnsupportedOperationException();
  }
}
