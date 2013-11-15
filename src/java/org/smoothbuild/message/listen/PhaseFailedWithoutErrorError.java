package org.smoothbuild.message.listen;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;

public class PhaseFailedWithoutErrorError extends Message {
  public PhaseFailedWithoutErrorError() {
    super(MessageType.ERROR,
        "Internal error: PhaseFailedException was thrown but no error is reported.");
  }
}
