package org.smoothbuild.task.err;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.ErrorCodeMessage;

public class NullResultError extends ErrorCodeMessage {
  public NullResultError(CallLocation callLocation) {
    super(callLocation.location(), "Faulty implementation of " + callLocation.name()
        + " function : 'null' was returned but no error reported.");
  }
}
