package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.match.PathMatcher.pathMatcher;

import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.match.IllegalPathPatternException;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;

import com.google.common.base.Predicate;

public class FilterFunction {
  public interface Parameters {
    @Required
    public FileSet files();

    @Required
    public String include();
  }

  @SmoothFunction("filter")
  public static FileSet execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public FileSet execute() {
      Predicate<Path> filter = createFilter();
      MutableFileSet result = sandbox.resultFileSet();

      for (File file : params.files()) {
        if (filter.apply(file.path())) {
          MutableFile destination = result.createFile(file.path());
          destination.setContent(file);
        }
      }

      return result;
    }

    private Predicate<Path> createFilter() {
      try {
        return pathMatcher(params.include());
      } catch (IllegalPathPatternException e) {
        throw new ErrorMessageException(new IllegalPathPatternError("include", e.getMessage()));
      }
    }
  }
}
