package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SString;

public class PathFunction {
  public static SString execute(NativeApi nativeApi, BuiltinSmoothModule.PathParameters params) {
    return nativeApi.string(params.file().path().value());
  }
}
