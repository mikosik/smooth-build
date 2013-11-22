package org.smoothbuild.lang.type;

import java.io.InputStream;

import org.smoothbuild.io.fs.base.Path;

/**
 * Smooth File. File value in smooth language.
 */
public interface SFile extends Value {
  public Path path();

  public SBlob content();

  public InputStream openInputStream();
}
