package org.smoothbuild.builtin.compress;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class UnzipFunction {
  public interface UnzipParameters {
    @Required
    public Blob blob();
  }

  @SmoothFunction
  public static SArray<SFile> unzip(NativeApi nativeApi, UnzipParameters params) {
    return new Unzipper(nativeApi).unzip(params.blob());
  }
}
