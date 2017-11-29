package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Struct;

public class FileToBlobFunction {
  @SmoothFunction
  public static Blob fileToBlob(NativeApi nativeApi, Struct file) {
    return (Blob) file.get("content");
  }
}
