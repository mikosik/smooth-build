package org.smoothbuild.fs.plugin;

import java.io.InputStream;

import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.Path;

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
  public InputStream createInputStream() {
    return file.createInputStream();
  }

}
