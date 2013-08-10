package org.smoothbuild.lang.internal;

import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Path;

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
  public Path fullPath() {
    return file.fullPath();
  }

  @Override
  public InputStream createInputStream() {
    return file.createInputStream();
  }

  @Override
  public OutputStream createOutputStream() {
    throw new UnsupportedOperationException();
  }
}
