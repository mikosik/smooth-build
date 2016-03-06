package org.smoothbuild.builtin.blob;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class FileFunction {
  @SmoothFunction
  public static SFile File(Container container, SString path, Blob content) {
    try {
      Path.path(path.value());
    } catch (IllegalArgumentException e) {
      throw new ErrorMessage("Param '" + "path" + "' has illegal value. " + e.getMessage());
    }
    return container.create().file(path, content);
  }
}
