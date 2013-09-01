package org.smoothbuild.parse.err;

import org.smoothbuild.problem.CodeError;
import org.smoothbuild.problem.CodeLocation;

public class DuplicateFunctionError extends CodeError {
  public DuplicateFunctionError(CodeLocation codeLocation, String name) {
    super(codeLocation, "Duplicate function '" + name + "'");
  }
}
