package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;

public class ContentFunction {
  public static SBlob execute(NativeApi nativeApi, BuiltinSmoothModule.ContentParameters params) {
    return params.file().content();
  }
}
