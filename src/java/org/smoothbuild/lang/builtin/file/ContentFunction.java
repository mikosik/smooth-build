package org.smoothbuild.lang.builtin.file;

import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.builtin.BuiltinSmoothModule;
import org.smoothbuild.task.exec.NativeApiImpl;

public class ContentFunction {
  public static SBlob execute(NativeApiImpl nativeApi, BuiltinSmoothModule.ContentParameters params) {
    return params.file().content();
  }
}
