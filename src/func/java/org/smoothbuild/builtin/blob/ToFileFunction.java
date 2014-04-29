package org.smoothbuild.builtin.blob;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.builtin.BuiltinSmoothModule;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.task.exec.NativeApiImpl;

public class ToFileFunction {

  public static SFile execute(NativeApiImpl nativeApi, BuiltinSmoothModule.ToFileParameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApi nativeApi;
    private final BuiltinSmoothModule.ToFileParameters params;

    public Worker(NativeApi nativeApi, BuiltinSmoothModule.ToFileParameters params) {
      this.nativeApi = nativeApi;
      this.params = params;
    }

    public SFile execute() {
      Path path = validatedPath("path", params.path());
      SBlob content = params.content();
      return nativeApi.file(path, content);
    }
  }
}
