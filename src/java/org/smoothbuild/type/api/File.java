package org.smoothbuild.type.api;

import java.io.InputStream;

import org.smoothbuild.fs.base.Path;


public interface File {
  public Path path();

  public InputStream openInputStream();

}
