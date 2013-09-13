package org.smoothbuild.builtin.file;

import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.plugin.api.Path.validationError;

import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.problem.ProblemsListener;

public class PathArgValidator {
  public static Path validatedPath(String name, String value, ProblemsListener problems) {
    String message = validationError(value);
    if (message != null) {
      problems.report(new IllegalPathError(name, message));
      return null;
    }

    return path(value);
  }
}
