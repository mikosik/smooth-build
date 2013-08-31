package org.smoothbuild.run.err;

import java.util.Collection;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.problem.Error;

public class UnknownFunctionError extends Error {

  public UnknownFunctionError(Name name, Collection<Name> availableNames) {
    super("Unknown function " + name + "\nOnly following function are available: "
        + availableNames.toString());
  }
}
