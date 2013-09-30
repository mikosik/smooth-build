package org.smoothbuild.parse.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.CodeMessage;

public class OverridenImportError extends CodeMessage {
  public OverridenImportError(CodeLocation codeLocation, String name, Name imported) {
    super(ERROR, codeLocation, "Function '" + name + "' overrides imported " + imported + ".");
  }
}
