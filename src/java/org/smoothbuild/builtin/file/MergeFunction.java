package org.smoothbuild.builtin.file;

import org.smoothbuild.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;

public class MergeFunction {

  public interface Parameters {
    @Required
    public FileSet files();

    @Required
    public FileSet with();
  }

  @SmoothFunction("merge")
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
      MutableFileSet result = sandbox.resultFileSet();

      for (File file : params.files()) {
        MutableFile destination = result.createFile(file.path());
        destination.setContent(file);
      }
      for (File file : params.with()) {
        MutableFile destination = result.createFile(file.path());
        if (result.contains(file.path())) {
          sandbox.report(new DuplicateMergedPathError(file.path()));
        }
        destination.setContent(file);
      }

      return result;
    }
  }
}
