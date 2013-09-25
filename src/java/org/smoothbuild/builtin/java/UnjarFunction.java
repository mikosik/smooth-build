package org.smoothbuild.builtin.java;

import java.io.IOException;

import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.err.FileSystemError;

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
        throw new FileSystemError(e);
      }
      return result;
    }
  }
}
