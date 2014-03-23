package org.smoothbuild.message.listen;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class PhaseFailedWithoutErrorError extends Message {
  public PhaseFailedWithoutErrorError() {
    super(ERROR, "Internal error: PhaseFailedException was thrown but no error was logged.");
  }
}
