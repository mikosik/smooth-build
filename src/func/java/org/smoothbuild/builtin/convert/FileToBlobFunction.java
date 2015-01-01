package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class FileToBlobFunction {

  public interface Parameters {
    @Required
    public SFile input();
  }

  @SmoothFunction
  public static Blob fileToBlob(NativeApi nativeApi, Parameters params) {
    return params.input().content();
  }
}
