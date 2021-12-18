package org.smoothbuild.slib.file;

import static org.smoothbuild.io.fs.base.Path.path;

import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.io.fs.base.IllegalPathExc;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.NativeApi;

public class PathArgValidator {
  public static Path validatedProjectPath(NativeApi nativeApi, String name, StringB path) {
    String value = path.toJ();
    switch (value) {
      case ".":
        return Path.root();
      case "":
        nativeApi.log().error("Param `" + name + "` has illegal value. Path cannot be empty.");
        return null;
      default:
        try {
          return path(value);
        } catch (IllegalPathExc e) {
          nativeApi.log().error("Param `" + name + "` has illegal value. " + e.getMessage());
          return null;
        }
    }
  }
}
