package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.SourceLocation;

public class OverridenImportError extends CodeError {
  public OverridenImportError(SourceLocation sourceLocation, String name, Name imported) {
    super(sourceLocation, "Function '" + name + "' overrides imported " + imported + ".");
  }
}
