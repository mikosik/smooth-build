package org.smoothbuild.builtin.compress;

import java.io.IOException;

import org.smoothbuild.builtin.compress.err.DuplicatePathInZipError;
import org.smoothbuild.builtin.compress.err.DuplicatePathInZipException;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipException;
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
      try {
        unzipper.unzipFile(params.file(), result);
      } catch (IOException e) {
        throw new FileSystemException(e);
      } catch (DuplicatePathInZipException e) {
        sandbox.report(new DuplicatePathInZipError(e.path()));
      } catch (IllegalPathInZipException e) {
        sandbox.report(new IllegalPathInZipError(e.fileName()));
      }
      return result;
    }
  }
}
