package org.smoothbuild.builtin.java;

import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

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
      FileSetBuilder result = sandbox.fileSetBuilder();
      unjarer.unjarFile(params.file(), result);
      return result.build();
    }
  }
}
