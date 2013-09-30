package org.smoothbuild.run.err;

import java.util.Collection;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.ErrorMessage;

public class UnknownFunctionError extends ErrorMessage {

  public UnknownFunctionError(Name name, Collection<Name> availableNames) {
    super("Unknown function " + name + "\nOnly following function are available: "
        + availableNames.toString());
  }
}
