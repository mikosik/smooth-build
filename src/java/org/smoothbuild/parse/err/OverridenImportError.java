package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.SourceLocation;

public class OverridenImportError extends Error {
  public OverridenImportError(SourceLocation sourceLocation, String name, Name imported) {
    super(sourceLocation, "Function '" + name + "' overrides imported " + imported + ".");
  }
}
