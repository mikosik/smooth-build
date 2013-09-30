package org.smoothbuild.parse.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.message.message.CodeLocation;

public class OverridenImportError extends ErrorCodeMessage {
  public OverridenImportError(CodeLocation codeLocation, String name, Name imported) {
    super(codeLocation, "Function '" + name + "' overrides imported " + imported + ".");
  }
}
