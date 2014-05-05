package org.smoothbuild.builtin.blob;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;

public class ToFileFunction {
  public static SFile execute(NativeApi nativeApi, BuiltinSmoothModule.ToFileParameters params) {
    Path path = validatedPath("path", params.path());
    SBlob content = params.content();
    return nativeApi.file(path, content);
  }
}
