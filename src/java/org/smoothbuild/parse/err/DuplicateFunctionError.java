package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.message.message.CodeLocation;

public class DuplicateFunctionError extends ErrorCodeMessage {
  public DuplicateFunctionError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Duplicate function '" + name + "'");
  }
}
