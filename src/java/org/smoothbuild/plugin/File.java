package org.smoothbuild.plugin;

import java.io.InputStream;

import org.smoothbuild.fs.base.Path;

public interface File extends Value {
  public Path path();

  public Blob content();

  public InputStream openInputStream();
}
