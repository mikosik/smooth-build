package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class PathFunction {
  @SmoothFunction
  public static SString path(NativeApi nativeApi, Struct file) {
    return (SString) file.get("path");
  }
}
