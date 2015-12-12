package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class FileToBlobFunction {
  @SmoothFunction
  public static Blob fileToBlob(Container container, SFile input) {
    return input.content();
  }
}
