package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.problem.Error;

public class NullResultError extends Error {
  public NullResultError(Name name) {
    super("Faulty plugin implementation of function " + name
        + ": 'null' was returned but no problem reported.");
  }
}
