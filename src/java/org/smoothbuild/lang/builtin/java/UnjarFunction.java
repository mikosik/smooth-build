package org.smoothbuild.lang.builtin.java;

import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;

public class UnjarFunction {
  public interface Parameters {
    @Required
    public File file();
  }

  @SmoothFunction(name = "unjar")
  public static Array<File> execute(Sandbox sandbox, Parameters params) {
    return new Unjarer(sandbox).unjarFile(params.file());
  }
}
