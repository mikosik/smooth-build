package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.CodeError;
import org.smoothbuild.message.message.CodeLocation;

public class DuplicateFunctionError extends CodeError {
  public DuplicateFunctionError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Duplicate function '" + name + "'");
  }
}
