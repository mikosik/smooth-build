package org.smoothbuild.builtin.compress;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;

public class UnzipFunction {
  public static SArray<SFile> execute(NativeApi nativeApi,
      BuiltinSmoothModule.UnzipParameters params) {
    return new Unzipper(nativeApi).unzip(params.blob());
  }
}
