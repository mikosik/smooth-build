package org.smoothbuild.parse.err;

import org.smoothbuild.problem.Error;
import org.smoothbuild.problem.SourceLocation;

public class DuplicateFunctionError extends Error {
  public DuplicateFunctionError(SourceLocation sourceLocation, String name) {
    super(sourceLocation, "Duplicate function '" + name + "'");
  }
}
