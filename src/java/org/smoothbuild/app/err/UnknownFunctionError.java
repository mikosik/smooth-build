package org.smoothbuild.app.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import java.util.Collection;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.Message;

public class UnknownFunctionError extends Message {
  public UnknownFunctionError(Name name, Collection<Name> availableNames) {
    super(ERROR, "Unknown function " + name + "\nOnly following function are available: "
        + availableNames.toString());
  }
}
