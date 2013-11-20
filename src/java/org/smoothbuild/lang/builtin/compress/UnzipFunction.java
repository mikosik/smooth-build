package org.smoothbuild.lang.builtin.compress;

import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class UnzipFunction {
  public interface Parameters {
    @Required
    public File file();
  }

  @SmoothFunction(name = "unzip")
  public static Array<File> execute(Sandbox sandbox, Parameters params) {
    return new Unzipper(sandbox).unzipFile(params.file());
  }
}
