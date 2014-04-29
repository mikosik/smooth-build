package org.smoothbuild.builtin.java;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;

public class UnjarFunction {
  public static SArray<SFile> execute(NativeApi nativeApi,
      BuiltinSmoothModule.UnjarParameters params) {
    return new Unjarer(nativeApi).unjar(params.blob());
  }
}
