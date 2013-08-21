package org.smoothbuild.plugin;

import java.io.InputStream;
import java.io.OutputStream;

public interface File {
  public Path path();

  /*
   * TODO should not be exposed to plugins but is needed by SaveToFunction
   */
  public Path fullPath();

  public InputStream createInputStream();

  public OutputStream createOutputStream();
}
