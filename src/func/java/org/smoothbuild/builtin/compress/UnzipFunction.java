package org.smoothbuild.builtin.compress;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class UnzipFunction {
  public interface UnzipParameters {
    @Required
    public Blob blob();
  }

  @SmoothFunction
  public static Array<SFile> unzip(NativeApi nativeApi, UnzipParameters params) {
    return new Unzipper(nativeApi).unzip(params.blob());
  }
}
