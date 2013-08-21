package org.smoothbuild.lang.internal;

import static org.smoothbuild.lang.internal.ImmutableFile.immutableFile;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Files;
import org.smoothbuild.plugin.Path;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

public class ImmutableFiles implements Files {
  private final Files files;

  public static Files immutableFiles(Files files) {
    if (files instanceof ImmutableFiles) {
      return files;
    } else {
      return new ImmutableFiles(files);
    }
  }

  public ImmutableFiles(Files files) {
    this.files = files;
  }

  @Override
  public File file(Path path) {
    return immutableFile(files.file(path));
  }

  @Override
  public Iterable<File> asIterable() {
    return FluentIterable.from(files.asIterable()).transform(new Function<File, File>() {
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
