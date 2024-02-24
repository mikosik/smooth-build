package org.smoothbuild.stdlib.file;

import static org.smoothbuild.common.filesystem.base.PathS.path;

import org.smoothbuild.common.filesystem.base.IllegalPathException;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class PathArgValidator {
  public static PathS validatedProjectPath(NativeApi nativeApi, String name, StringB path)
      throws BytecodeException {
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
        } catch (IllegalPathException e) {
          nativeApi.log().error("Param `" + name + "` has illegal value. " + e.getMessage());
          return null;
        }
    }
  }
}
