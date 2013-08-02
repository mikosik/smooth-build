package org.smoothbuild.lang.type;

import java.io.OutputStream;

public interface FileRw extends FileRo {
  public OutputStream createOutputStream();
}
