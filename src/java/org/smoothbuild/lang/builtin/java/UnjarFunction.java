package org.smoothbuild.lang.builtin.java;

import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;

public class UnjarFunction {
  public interface Parameters {
    @Required
    public SFile file();
  }

  @SmoothFunction(name = "unjar")
  public static SArray<SFile> execute(Sandbox sandbox, Parameters params) {
    return new Unjarer(sandbox).unjarFile(params.file());
  }
}
