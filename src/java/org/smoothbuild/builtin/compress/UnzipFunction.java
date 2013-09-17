package org.smoothbuild.builtin.compress;

import java.io.IOException;

import org.smoothbuild.fs.base.exc.FileSystemException;
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
    Unzipper unzipper = new Unzipper();
    MutableFileSet result = sandbox.resultFileSet();
    try {
      unzipper.unzipFile(params.file(), result);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return result;
  }
}
