package org.smoothbuild.parse.err;

import org.smoothbuild.message.message.ErrorCodeMessage;
import org.smoothbuild.message.message.CodeLocation;

public class UndefinedFunctionError extends ErrorCodeMessage {
  public UndefinedFunctionError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Undefined function  '" + name + "'");
  }
}
