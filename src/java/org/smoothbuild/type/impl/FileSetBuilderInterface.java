package org.smoothbuild.type.impl;

import java.io.OutputStream;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;

// TODO
// remove once migration away from MutableFileSet/MutableFile is complete
public interface FileSetBuilderInterface {
  public void add(File file);

  public OutputStream openFileOutputStream(Path path);

  public boolean contains(Path path);
}
