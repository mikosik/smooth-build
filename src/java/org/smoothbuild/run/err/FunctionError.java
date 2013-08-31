package org.smoothbuild.run.err;

import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.problem.Error;

public class FunctionError extends Error {
  public FunctionError(FunctionException exception) {
    // TODO smooth stack trace pointing to faulty call in smooth script should
    // be added here
    super(exception.getClass().getSimpleName() + ": " + exception.getMessage());
  }
}
