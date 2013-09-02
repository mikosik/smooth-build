package org.smoothbuild.task.err;

import org.smoothbuild.problem.Error;

public class NullResultError extends Error {
  public NullResultError() {
    super("Faulty plugin implementation: 'null' value was returned but no problem reported.");
  }
}
