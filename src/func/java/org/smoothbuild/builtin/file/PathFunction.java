package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class PathFunction {
  @SmoothFunction
  public static SString path(Container container, SFile file) {
    return container.create().string(file.path().value());
  }
}
