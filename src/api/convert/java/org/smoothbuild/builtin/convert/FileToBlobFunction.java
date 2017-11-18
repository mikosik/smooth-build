package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class FileToBlobFunction {
  @SmoothFunction
  public static Blob fileToBlob(NativeApi nativeApi, SFile file) {
    return file.content();
  }
}
