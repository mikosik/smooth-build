package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.match.PathMatcher.pathMatcher;

import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.match.IllegalPathPatternException;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.FileSetBuilder;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.base.Predicate;

public class FilterFunction {
  public interface Parameters {
    @Required
    public FileSet files();

    @Required
    public StringValue include();
  }

  @SmoothFunction(name = "filter")
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
      FileSetBuilder builder = sandbox.fileSetBuilder();

      for (File file : params.files()) {
        if (filter.apply(file.path())) {
          builder.add(file);
        }
      }

      return builder.build();
    }

    private Predicate<Path> createFilter() {
      try {
        return pathMatcher(params.include().value());
      } catch (IllegalPathPatternException e) {
        throw new ErrorMessageException(new IllegalPathPatternError("include", e.getMessage()));
      }
    }
  }
}
