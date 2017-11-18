package org.smoothbuild.acceptance.lang.nativ;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SFile;

public class FileParameter {
  @SmoothFunction
  public static SFile fileParameter(NativeApi nativeApi, SFile file) {
    return file;
  }
}
