package org.smoothbuild.lang.type;

import java.io.InputStream;

public interface Blob extends Value {
  public InputStream openInputStream();
}
