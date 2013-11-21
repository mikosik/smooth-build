package org.smoothbuild.lang.builtin.compress;

import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;

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
