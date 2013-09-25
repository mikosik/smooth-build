package org.smoothbuild.parse.err;

import org.smoothbuild.message.CodeError;
import org.smoothbuild.message.CodeLocation;

@SuppressWarnings("serial")
public class UndefinedFunctionError extends CodeError {
  public UndefinedFunctionError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Undefined function  '" + name + "'");
  }
}
