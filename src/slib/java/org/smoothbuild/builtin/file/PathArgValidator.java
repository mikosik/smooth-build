package org.smoothbuild.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;

public class PathArgValidator {
  private static final String PROJECT_ROOT = "//";

  public static Path validatedProjectPath(NativeApi nativeApi, String name, SString stringValue) {
    String value = stringValue.jValue();
    if (!value.startsWith(PROJECT_ROOT)) {
      nativeApi.log().error("Param '" + name + "' has illegal value. It should start with \""
          + PROJECT_ROOT + "\" which represents project's root dir.");
      throw new AbortException();
    }
    return validatedPath(nativeApi, name, stringValue.jValue().substring(PROJECT_ROOT.length()));
  }

  private static Path validatedPath(NativeApi nativeApi, String name, String value) {
    try {
      return path(value);
    } catch (IllegalPathException e) {
      nativeApi.log().error("Param '" + name + "' has illegal value. " + e.getMessage());
      throw new AbortException();
    }
  }
}
