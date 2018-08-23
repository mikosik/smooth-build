package org.smoothbuild.builtin.java;

import java.io.IOException;

import org.smoothbuild.builtin.compress.UnzipFunction;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;

public class UnjarFunction {
  @SmoothFunction
  public static Array unjar(NativeApi nativeApi, Blob jar, Array javaHash) throws IOException {
    return UnzipFunction.unzip(nativeApi, jar, string -> !string.equals("META-INF/MANIFEST.MF"));
  }
}
