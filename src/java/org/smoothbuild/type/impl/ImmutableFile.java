package org.smoothbuild.type.impl;

import java.io.InputStream;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;

public class ImmutableFile implements File {
  private final File file;

  public static File immutableFile(File file) {
    if (file instanceof ImmutableFile) {
      return file;
    } else {
      return new ImmutableFile(file);
    }
  }

  private ImmutableFile(File file) {
    this.file = file;
  }

  @Override
  public Path path() {
    return file.path();
  }

  @Override
  public InputStream openInputStream() {
    return file.openInputStream();
  }

}
