package org.smoothbuild.builtin.java;

import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;
import org.smoothbuild.type.api.MutableFileSet;

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
      unjarer.unjarFile(params.file(), result);
      return result;
    }
  }
}
