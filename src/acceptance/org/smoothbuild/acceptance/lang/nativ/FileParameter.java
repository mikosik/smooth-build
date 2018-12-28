package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Struct;

public class FileParameter {
  @SmoothFunction("fileParameter")
  public static Struct fileParameter(NativeApi nativeApi, Struct file) {
    return file;
  }
}
