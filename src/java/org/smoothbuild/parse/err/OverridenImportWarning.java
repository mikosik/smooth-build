package org.smoothbuild.parse.err;

import static org.smoothbuild.problem.ProblemType.WARNING;

import org.smoothbuild.lang.function.CanonicalName;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.SourceLocation;

public class OverridenImportWarning extends Problem {
  public OverridenImportWarning(SourceLocation sourceLocation, String name, CanonicalName imported) {
    super(WARNING, sourceLocation, "Function '" + name + "' overrides imported '" + imported.full()
        + "'");
  }
}
