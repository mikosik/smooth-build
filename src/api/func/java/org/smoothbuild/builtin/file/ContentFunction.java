package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Struct;

public class ContentFunction {
  @SmoothFunction
  public static Blob content(NativeApi nativeApi, Struct file) {
    return (Blob) file.get("content");
  }
}
