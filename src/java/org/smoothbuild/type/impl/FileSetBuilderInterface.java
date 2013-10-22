package org.smoothbuild.type.impl;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;

// TODO
// remove once migration away from MutableFileSet/MutableFile is complete
public interface FileSetBuilderInterface {
  public void add(File file);

  public boolean contains(Path path);
}
