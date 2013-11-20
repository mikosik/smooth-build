package org.smoothbuild.lang.builtin.file;

import org.smoothbuild.lang.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.plugin.FileSetBuilder;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class MergeFunction {

  public interface Parameters {
    @Required
    public Array<File> files();

    @Required
    public Array<File> with();
  }

  @SmoothFunction(name = "merge")
  public static Array<File> execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  public static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public Array<File> execute() {
      FileSetBuilder builder = sandbox.fileSetBuilder();

      for (File file : params.files()) {
        builder.add(file);
      }
      for (File file : params.with()) {
        if (builder.contains(file.path())) {
          sandbox.report(new DuplicateMergedPathError(file.path()));
        } else {
          builder.add(file);
        }
      }

      return builder.build();
    }
  }
}
