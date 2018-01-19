package org.smoothbuild.builtin.blob;

import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;

public class FileFunction {
  @SmoothFunction
  public static Struct File(NativeApi nativeApi, SString path, Blob content) {
    try {
      Path.path(path.data());
    } catch (IllegalPathException e) {
      nativeApi.log().error("Param '" + "path" + "' has illegal value. " + e.getMessage());
      return null;
    }
    return nativeApi.create().file(path, content);
  }
}
