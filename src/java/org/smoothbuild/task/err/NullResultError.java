package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.Error;

@SuppressWarnings("serial")
public class NullResultError extends Error {
  public NullResultError(Name name) {
    super("Faulty implementation of " + name
        + " function : 'null' was returned but no error reported.");
  }
}
