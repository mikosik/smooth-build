package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.task.exec.NativeApiImpl;

public class ContentFunction {
  public static SBlob execute(NativeApiImpl nativeApi, BuiltinSmoothModule.ContentParameters params) {
    return params.file().content();
  }
}
