package org.smoothbuild.slib.file;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.record.base.RString;
import org.smoothbuild.io.fs.base.IllegalPathException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.NativeApi;

public class PathArgValidator {
  public static Path validatedProjectPath(NativeApi nativeApi, String name, RString path) {
    String value = path.jValue();
    switch (value) {
      case ".":
        return Path.root();
      case "":
        nativeApi.log().error("Param '" + name + "' has illegal value. Path cannot be empty.");
        return null;
      default:
        try {
          return path(value);
        } catch (IllegalPathException e) {
          nativeApi.log().error("Param '" + name + "' has illegal value. " + e.getMessage());
          return null;
        }
    }
  }
}
