package org.smoothbuild.builtin.file;

import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.plugin.Path.validationError;

import org.smoothbuild.builtin.file.err.IllegalPathError;
import org.smoothbuild.builtin.file.err.MissingRequiredArgError;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.ProblemsListener;

public class PathArgValidator {
  public static Path validatedPath(String name, String value, ProblemsListener problems) {
    if (value == null) {
      problems.report(new MissingRequiredArgError(name));
      return null;
    }
    String message = validationError(value);
    if (message != null) {
      problems.report(new IllegalPathError(name, message));
      return null;
    }

    return path(value);
  }
}
