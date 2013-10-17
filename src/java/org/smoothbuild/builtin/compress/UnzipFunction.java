package org.smoothbuild.builtin.compress;

import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;
import org.smoothbuild.type.impl.FileSetBuilder;

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
      FileSetBuilder fileSetBuilder = sandbox.fileSetBuilder();
      unzipper.unzipFile(params.file(), fileSetBuilder);
      return fileSetBuilder.build();
    }
  }
}
