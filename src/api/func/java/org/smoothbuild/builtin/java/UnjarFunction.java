package org.smoothbuild.builtin.java;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;

public class UnjarFunction {
  @SmoothFunction
  public static Array unjar(NativeApi nativeApi, Blob jar, SString javaVersion) {
    return UnzipFunction.unzip(nativeApi, jar, string -> !string.equals("META-INF/MANIFEST.MF"));
  }
}
