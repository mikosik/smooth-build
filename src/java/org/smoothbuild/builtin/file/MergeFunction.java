package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;

public class MergeFunction {

  public interface Parameters {
    @Required
    public FileSet files();

    @Required
    public FileSet with();
  }

  @SmoothFunction(name = "merge")
  public static FileSet execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  public static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public FileSet execute() {
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
