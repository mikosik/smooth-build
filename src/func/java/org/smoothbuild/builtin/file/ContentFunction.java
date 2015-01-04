package org.smoothbuild.builtin.file;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunctionLegacy;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;

public class ContentFunction {
  public interface ContentParameters {
    @Required
    public SFile file();
  }

  @SmoothFunctionLegacy
  public static Blob content(NativeApi nativeApi, ContentParameters params) {
    return params.file().content();
  }
}
