package org.smoothbuild.plugin;

import java.io.InputStream;

public interface Blob extends Value {
  public InputStream openInputStream();
}
