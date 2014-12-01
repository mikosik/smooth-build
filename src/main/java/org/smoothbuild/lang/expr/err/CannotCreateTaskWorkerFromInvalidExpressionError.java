package org.smoothbuild.lang.expr.err;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.message.base.Message;

public class CannotCreateTaskWorkerFromInvalidExpressionError extends Message {
  public CannotCreateTaskWorkerFromInvalidExpressionError() {
    super(FATAL, "Cannot create TaskWorker from InvalidExpression.");
  }
}
