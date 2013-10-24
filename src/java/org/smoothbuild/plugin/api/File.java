package org.smoothbuild.plugin.api;

import java.io.InputStream;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.Hashed;

public interface File extends Hashed {
  public Path path();

  public InputStream openInputStream();
}
