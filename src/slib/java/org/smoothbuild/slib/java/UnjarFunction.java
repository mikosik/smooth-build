package org.smoothbuild.slib.java;

import java.io.IOException;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.slib.compress.UnzipFunction;

public class UnjarFunction {
  @SmoothFunction("unjar")
  public static Array unjar(NativeApi nativeApi, Blob jar) throws IOException {
    return UnzipFunction.unzip(nativeApi, jar, string -> !string.equals("META-INF/MANIFEST.MF"));
  }
}
