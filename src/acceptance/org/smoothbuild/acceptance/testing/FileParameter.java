package org.smoothbuild.acceptance.testing;

import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.plugin.NativeImplementation;

public class FileParameter {
  @NativeImplementation("fileParameter")
  public static Tuple fileParameter(NativeApi nativeApi, Tuple file) {
    return file;
  }
}
