package org.smoothbuild.lang.type;

import java.io.InputStream;

/**
 * Smooth Blob. Blob value in smooth language.
 */
public interface SBlob extends SValue {
  public InputStream openInputStream();
}
