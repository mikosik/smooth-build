package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.fs.match.PathMatcher.pathMatcher;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.match.IllegalPathPatternException;
import org.smoothbuild.lang.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.lang.plugin.FileSetBuilder;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.base.Predicate;

public class FilterFunction {
  public interface Parameters {
    @Required
    public Array<File> files();

    @Required
    public StringValue include();
  }

  @SmoothFunction(name = "filter")
  public static Array<File> execute(SandboxImpl sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public Array<File> execute() {
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
