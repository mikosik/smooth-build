package org.smoothbuild.plugin.api;

import java.io.OutputStream;

public interface MutableFile extends File {
  public OutputStream openOutputStream();

  public void setContent(File file);
}
