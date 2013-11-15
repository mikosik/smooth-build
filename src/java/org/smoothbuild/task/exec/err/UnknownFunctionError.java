package org.smoothbuild.task.exec.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Collection;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;

public class UnknownFunctionError extends Message {
  public UnknownFunctionError(Name name, Collection<Name> availableNames) {
    super(ERROR, "Unknown function " + name + "\nOnly following function are available: "
        + availableNames.toString());
  }
}
