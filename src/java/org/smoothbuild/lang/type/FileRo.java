package org.smoothbuild.lang.type;

import java.io.InputStream;

public interface FileRo {
  public Path path();

  public InputStream createInputStream();
}
