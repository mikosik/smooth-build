package org.smoothbuild.builtin.blob;

import static org.smoothbuild.io.fs.base.Path.validationError;

import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class FileFunction {
  @SmoothFunction
  public static SFile File(Container container, SString path, Blob content) {
    String message = validationError(path.value());
    if (message != null) {
      throw new ErrorMessage("Param '" + "path" + "' has illegal value. " + message);
    }
    return container.create().file(path, content);
  }
}
