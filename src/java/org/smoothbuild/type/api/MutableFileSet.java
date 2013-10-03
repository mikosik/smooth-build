package org.smoothbuild.type.api;

import org.smoothbuild.fs.base.Path;


public interface MutableFileSet extends FileSet {
  public MutableFile createFile(Path path);
}
