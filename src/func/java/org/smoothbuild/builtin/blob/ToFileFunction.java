package org.smoothbuild.builtin.blob;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class ToFileFunction {
  @SmoothFunction
  public static SFile toFile(Container container, SString path, Blob content) {
    validatedPath("path", path);
    return container.create().file(path, content);
  }
}
