package org.smoothbuild.builtin.blob;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

public class ToFileFunction {
  @SmoothFunction
  public static SFile toFile( //
      NativeApi nativeApi, //
      @Required @Name("path") SString path, //
      @Required @Name("content") Blob content) {
    return nativeApi.file(validatedPath("path", path), content);
  }
}
