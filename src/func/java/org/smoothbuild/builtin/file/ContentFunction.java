package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ContentFunction {
  public interface ContentParameters {
    @Required
    public SFile file();
  }

  @SmoothFunction
  public static Blob content(NativeApi nativeApi, ContentParameters params) {
    return params.file().content();
  }
}
