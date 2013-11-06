package org.smoothbuild.builtin.compress;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;

public class UnzipFunction {
  public interface Parameters {
    @Required
    public File file();
  }

  @SmoothFunction(name = "unzip")
  public static FileSet execute(Sandbox sandbox, Parameters params) {
    return new Unzipper(sandbox).unzipFile(params.file());
  }
}
