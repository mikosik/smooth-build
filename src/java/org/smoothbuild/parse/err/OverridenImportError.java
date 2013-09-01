package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.CodeLocation;

public class OverridenImportError extends CodeError {
  public OverridenImportError(CodeLocation codeLocation, String name, Name imported) {
    super(codeLocation, "Function '" + name + "' overrides imported " + imported + ".");
  }
}
