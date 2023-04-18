package org.smoothbuild.stdlib.file;

import static org.smoothbuild.fs.base.PathS.path;

import org.smoothbuild.fs.base.IllegalPathExc;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class PathArgValidator {
  public static PathS validatedProjectPath(NativeApi nativeApi, String name, StringB path) {
    String value = path.toJ();
    switch (value) {
      case ".":
        return PathS.root();
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
