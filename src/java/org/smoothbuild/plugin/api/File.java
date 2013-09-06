package org.smoothbuild.plugin.api;

import java.io.InputStream;

public interface File {
  public Path path();

  public InputStream openInputStream();

}
