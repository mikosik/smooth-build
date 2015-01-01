package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

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
