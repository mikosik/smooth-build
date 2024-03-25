package org.smoothbuild.stdlib.file;

import static org.smoothbuild.common.bucket.base.Path.path;

import org.smoothbuild.common.bucket.base.IllegalPathException;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class PathArgValidator {
  public static Path validatedProjectPath(NativeApi nativeApi, String name, BString path)
      throws BytecodeException {
    String value = path.toJavaString();
    switch (value) {
      case ".":
        return Path.root();
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
