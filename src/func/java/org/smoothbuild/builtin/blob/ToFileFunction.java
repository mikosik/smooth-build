package org.smoothbuild.builtin.blob;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunctionLegacy;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class ToFileFunction {
  public interface ToFileParameters {
    @Required
    public SString path();

    @Required
    public Blob content();
  }

  @SmoothFunctionLegacy
  public static SFile toFile(NativeApi nativeApi, ToFileParameters params) {
    Path path = validatedPath("path", params.path());
    Blob content = params.content();
    return nativeApi.file(path, content);
  }
}
