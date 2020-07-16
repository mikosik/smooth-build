package org.smoothbuild.acceptance.testing;

import org.smoothbuild.lang.object.base.Tuple;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class FileParameter {
  @SmoothFunction("fileParameter")
  public static Tuple fileParameter(NativeApi nativeApi, Tuple file) {
    return file;
  }
}
