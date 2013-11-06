package org.smoothbuild.builtin.java;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;

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
