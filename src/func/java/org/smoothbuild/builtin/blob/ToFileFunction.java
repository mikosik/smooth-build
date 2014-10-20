package org.smoothbuild.builtin.blob;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ToFileFunction {
  public interface ToFileParameters {
    @Required
    public SString path();

    @Required
    public SBlob content();
  }

  @SmoothFunction
  public static SFile toFile(NativeApi nativeApi, ToFileParameters params) {
    Path path = validatedPath("path", params.path());
    SBlob content = params.content();
    return nativeApi.file(path, content);
  }
}
