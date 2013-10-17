package org.smoothbuild.type.api;

import java.io.OutputStream;

import org.smoothbuild.fs.base.Path;

public interface MutableFileSet extends FileSet {
  public MutableFile createFile(Path path);

  public OutputStream openFileOutputStream(Path path);
}
