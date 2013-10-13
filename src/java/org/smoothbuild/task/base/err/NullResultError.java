package org.smoothbuild.task.base.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.CallLocation;

public class NullResultError extends CodeMessage {
  public NullResultError(CallLocation callLocation) {
    super(ERROR, callLocation.location(), "Faulty implementation of " + callLocation.name()
        + " function : 'null' was returned but no error reported.");
  }
}
