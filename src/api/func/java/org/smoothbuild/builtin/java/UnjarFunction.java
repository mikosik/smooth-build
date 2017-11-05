package org.smoothbuild.builtin.java;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;

public class UnjarFunction {
  @SmoothFunction
  public static Array unjar(Container container, Blob jar) {
    return UnzipFunction.unzip(container, jar, string -> !string.equals("META-INF/MANIFEST.MF"));
  }
}
