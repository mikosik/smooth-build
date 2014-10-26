package org.smoothbuild.lang.base;

import java.io.InputStream;

/**
 * Smooth Blob. Blob value in smooth language.
 */
public interface Blob extends Value {
  public InputStream openInputStream();
}
