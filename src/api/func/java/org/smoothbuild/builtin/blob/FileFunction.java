package org.smoothbuild.builtin.blob;

import static org.smoothbuild.lang.message.MessageException.errorException;

import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.SString;

public class FileFunction {
  @SmoothFunction
  public static Struct File(NativeApi nativeApi, SString path, Blob content) {
    try {
      Path.path(path.data());
    } catch (IllegalPathException e) {
      throw errorException("Param '" + "path" + "' has illegal value. " + e.getMessage());
    }
    return nativeApi.create().file(path, content);
  }
}
