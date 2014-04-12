package org.smoothbuild.lang.builtin.blob;

import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
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
      return createFile(validatedPath("path", params.path()));
    }

    private SFile createFile(Path filePath) {
      FileBuilder fileBuilder = nativeApi.fileBuilder();
      fileBuilder.setPath(filePath);
      fileBuilder.setContent(params.content());
      return fileBuilder.build();
    }
  }
}
