package org.smoothbuild.lang.function.def.err;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class CannotCreateTaskWorkerFromInvalidNodeError extends Message {
  public CannotCreateTaskWorkerFromInvalidNodeError() {
    super(FATAL, "Cannot create TaskWorker from InvalidNode.");
  }
}