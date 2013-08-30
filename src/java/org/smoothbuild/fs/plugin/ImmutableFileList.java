package org.smoothbuild.fs.plugin;

import static org.smoothbuild.fs.plugin.ImmutableFile.immutableFile;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileList;
import org.smoothbuild.plugin.Path;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class ImmutableFileList implements FileList {
  private final FileList fileList;

  public static FileList immutableFiles(FileList fileList) {
    if (fileList instanceof ImmutableFileList) {
      return fileList;
    } else {
      return new ImmutableFileList(fileList);
    }
  }

  public ImmutableFileList(FileList fileList) {
    this.fileList = fileList;
  }

  @Override
  public File file(Path path) {
    return immutableFile(fileList.file(path));
  }

  @Override
  public Iterable<File> asIterable() {
    return FluentIterable.from(fileList.asIterable()).transform(new Function<File, File>() {
      public File apply(File file) {
        return immutableFile(file);
      }
    });
  }

  @Override
  public File createFile(Path path) {
    throw new UnsupportedOperationException();
  }
}
