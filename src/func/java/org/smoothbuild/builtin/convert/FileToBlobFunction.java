package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class FileToBlobFunction {
  @SmoothFunction
  public static Blob fileToBlob( //
      NativeApi nativeApi, //
      @Required @Name("input") SFile input) {
    return input.content();
  }
}
