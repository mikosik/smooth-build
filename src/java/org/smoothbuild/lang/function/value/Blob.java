package org.smoothbuild.lang.function.value;

import java.io.InputStream;

public interface Blob extends Value {
  public InputStream openInputStream();
}
