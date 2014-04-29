package org.smoothbuild.lang.builtin.compress;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.builtin.BuiltinSmoothModule;

public class UnzipFunction {
  public static SArray<SFile> execute(NativeApi nativeApi,
      BuiltinSmoothModule.UnzipParameters params) {
    return new Unzipper(nativeApi).unzip(params.blob());
  }
}
