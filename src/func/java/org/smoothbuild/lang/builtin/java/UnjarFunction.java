package org.smoothbuild.lang.builtin.java;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.builtin.BuiltinSmoothModule;

public class UnjarFunction {
  public static SArray<SFile> execute(NativeApi nativeApi,
      BuiltinSmoothModule.UnjarParameters params) {
    return new Unjarer(nativeApi).unjar(params.blob());
  }
}
