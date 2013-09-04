package org.smoothbuild.plugin;

import java.io.OutputStream;

public interface MutableFile extends File {
  public OutputStream createOutputStream();
}
