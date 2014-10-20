package org.smoothbuild.builtin.convert;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class FileToBlobFunction {

  public interface Parameters {
    @Required
    public SFile input();
  }

  @SmoothFunction
  public static SBlob fileToBlob(NativeApi nativeApi, Parameters params) {
    return params.input().content();
  }
}
