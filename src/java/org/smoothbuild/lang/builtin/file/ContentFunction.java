package org.smoothbuild.lang.builtin.file;

import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.task.exec.NativeApiImpl;

public class ContentFunction {
  public interface Parameters {
    @Required
    public SFile file();
  }

  @SmoothFunction(name = "content")
  public static SBlob execute(NativeApiImpl nativeApi, Parameters params) {
    return params.file().content();
  }
}
