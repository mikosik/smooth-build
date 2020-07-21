package org.smoothbuild.acceptance.testing;

import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.record.base.Tuple;

public class FileParameter {
  @SmoothFunction("fileParameter")
  public static Tuple fileParameter(NativeApi nativeApi, Tuple file) {
    return file;
  }
}
