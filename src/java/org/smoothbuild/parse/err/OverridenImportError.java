package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.CodeError;
import org.smoothbuild.message.message.CodeLocation;

public class OverridenImportError extends CodeError {
  public OverridenImportError(CodeLocation codeLocation, String name, Name imported) {
    super(codeLocation, "Function '" + name + "' overrides imported " + imported + ".");
  }
}
