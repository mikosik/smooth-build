package org.smoothbuild.type.api;

import org.smoothbuild.fs.base.Path;


public interface FileSet extends Iterable<File> {
  public boolean contains(Path path);

  public File file(Path path);

}
