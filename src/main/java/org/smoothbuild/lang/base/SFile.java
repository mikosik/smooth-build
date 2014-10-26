package org.smoothbuild.lang.base;

import org.smoothbuild.io.fs.base.Path;

/**
 * Smooth File. File value in smooth language.
 */
public interface SFile extends Value {
  public Path path();

  public Blob content();
}
