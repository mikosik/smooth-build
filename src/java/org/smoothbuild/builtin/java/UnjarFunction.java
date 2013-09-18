package org.smoothbuild.builtin.java;

import java.io.IOException;

import org.smoothbuild.builtin.java.err.DuplicatePathInJarError;
import org.smoothbuild.builtin.java.err.DuplicatePathInJarException;
import org.smoothbuild.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.builtin.java.err.IllegalPathInJarException;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;

public class UnjarFunction {
  public interface Parameters {
    @Required
    public File file();
  }

  @SmoothFunction("unjar")
  public static FileSet execute(Sandbox sandbox, Parameters params) {
    return new Worker().execute(sandbox, params);
  }

  public static class Worker {
    private final Unjarer unjarer;

    public Worker() {
      this(new Unjarer());
    }

    public Worker(Unjarer unjarer) {
      this.unjarer = unjarer;
    }

    public FileSet execute(Sandbox sandbox, Parameters params) {
      MutableFileSet result = sandbox.resultFileSet();
      try {
        unjarer.unjarFile(params.file(), result);
      } catch (IOException e) {
        throw new FileSystemException(e);
      } catch (DuplicatePathInJarException e) {
        sandbox.report(new DuplicatePathInJarError(e.path()));
      } catch (IllegalPathInJarException e) {
        sandbox.report(new IllegalPathInJarError(e.fileName()));
      }
      return result;
    }
  }
}
