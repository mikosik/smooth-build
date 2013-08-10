package org.smoothbuild.lang.type;

import java.io.InputStream;
import java.io.OutputStream;

public interface File {
  public Path path();

  public InputStream createInputStream();

  public OutputStream createOutputStream();
}
