package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SFile;

public class FileParameter {
  @SmoothFunction
  public static SFile fileParameter(Container container, SFile file) {
    return file;
  }
}
