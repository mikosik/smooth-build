package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.SmoothFunction;

public class FileParameter {
  @SmoothFunction("fileParameter")
  public static Tuple fileParameter(NativeApi nativeApi, Tuple file) {
    return file;
  }
}
