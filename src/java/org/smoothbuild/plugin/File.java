package org.smoothbuild.plugin;

import java.io.InputStream;

public interface File {
  public Path path();

  public InputStream createInputStream();

}
