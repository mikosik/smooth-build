package org.smoothbuild.lang.builtin.compress;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;

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
