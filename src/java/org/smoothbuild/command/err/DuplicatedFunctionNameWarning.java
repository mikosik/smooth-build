package org.smoothbuild.command.err;

import static org.smoothbuild.message.message.MessageType.WARNING;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.Message;

public class DuplicatedFunctionNameWarning extends Message {
  public DuplicatedFunctionNameWarning(Name name) {
    super(WARNING, "Function " + name + " has been specified more than once.");
  }
}
