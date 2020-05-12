package org.smoothbuild.slib.file;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;

public class PathArgValidator {
  public static Path validatedProjectPath(NativeApi nativeApi, String name, SString path) {
    try {
      String value = path.jValue();
      switch (value) {
        case "**":
          return Path.root();
        case "":
          return fail(nativeApi, name, "Path cannot be empty.");
        default:
          return path(value);
      }
    } catch (IllegalPathException e) {
      return fail(nativeApi, name, e.getMessage());
    }
  }

  private static Path fail(NativeApi nativeApi, String name, String message) {
    nativeApi.log().error("Param '" + name + "' has illegal value. " + message);
    throw new AbortException();
  }
}
