package org.smoothbuild.builtin.compress;

import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class UnzipFunction {
  @SmoothFunction
  public static Array<SFile> unzip( //
      NativeApi nativeApi, //
      @Required @Name("blob") Blob blob) {
    return new Unzipper(nativeApi).unzip(blob);
  }
}
