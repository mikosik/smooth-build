package org.smoothbuild.lang.builtin.java;

import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.FileSet;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class UnjarFunction {
  public interface Parameters {
    @Required
    public File file();
  }

  @SmoothFunction(name = "unjar")
  public static FileSet execute(Sandbox sandbox, Parameters params) {
    return new Unjarer(sandbox).unjarFile(params.file());
  }
}
