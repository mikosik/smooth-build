package org.smoothbuild.lang.builtin.blob;

import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.NativeApiImpl;

public class ToFileFunction {

  public interface Parameters {
    @Required
    public SString path();

    @Required
    public SBlob content();
  }

  @SmoothFunction(name = "toFile")
  public static SFile execute(NativeApiImpl nativeApi, Parameters params) {
    return new Worker(nativeApi, params).execute();
  }

  private static class Worker {
    private final NativeApi nativeApi;
    private final Parameters params;

    public Worker(NativeApi nativeApi, Parameters params) {
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
