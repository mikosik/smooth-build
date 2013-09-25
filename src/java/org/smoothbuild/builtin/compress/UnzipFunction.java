package org.smoothbuild.builtin.compress;

import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;

public class UnzipFunction {
  public interface Parameters {
    @Required
    public File file();
  }

  @SmoothFunction("unzip")
  public static FileSet execute(Sandbox sandbox, Parameters params) {
    return new Worker().execute(sandbox, params);
  }

  public static class Worker {
    private final Unzipper unzipper;

    public Worker() {
      this(new Unzipper());
    }

    public Worker(Unzipper unzipper) {
      this.unzipper = unzipper;
    }

    public FileSet execute(Sandbox sandbox, Parameters params) {
      MutableFileSet result = sandbox.resultFileSet();
      unzipper.unzipFile(params.file(), result);
      return result;
    }
  }
}
