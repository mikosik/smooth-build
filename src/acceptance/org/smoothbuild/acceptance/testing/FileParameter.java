package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.plugin.NativeApi;

public class FileParameter {
  public static Tuple function(NativeApi nativeApi, Tuple file) {
    return file;
  }
}
