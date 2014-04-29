package org.smoothbuild.lang.builtin.file;

import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.builtin.BuiltinSmoothModule;
import org.smoothbuild.task.exec.NativeApiImpl;

public class PathFunction {
  public static SString execute(NativeApiImpl nativeApi, BuiltinSmoothModule.PathParameters params) {
    return nativeApi.string(params.file().path().value());
  }
}
