package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class ContentFunction {
  @SmoothFunction
  public static Blob content( //
      NativeApi nativeApi, //
      @Required @Name("file") SFile file) {
    return file.content();
  }
}
