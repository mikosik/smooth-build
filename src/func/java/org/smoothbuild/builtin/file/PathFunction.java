package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.task.exec.NativeApiImpl;

public class PathFunction {
  public static SString execute(NativeApiImpl nativeApi, BuiltinSmoothModule.PathParameters params) {
    return nativeApi.string(params.file().path().value());
  }
}
