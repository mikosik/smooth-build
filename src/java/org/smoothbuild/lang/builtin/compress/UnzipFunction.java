package org.smoothbuild.lang.builtin.compress;

import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class UnzipFunction {
  public interface Parameters {
    @Required
    public SBlob blob();
  }

  @SmoothFunction(name = "unzip")
  public static SArray<SFile> execute(NativeApi nativeApi, Parameters params) {
    return new Unzipper(nativeApi).unzip(params.blob());
  }
}
