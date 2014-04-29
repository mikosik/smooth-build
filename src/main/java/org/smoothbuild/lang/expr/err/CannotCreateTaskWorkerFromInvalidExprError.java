package org.smoothbuild.lang.expr.err;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class CannotCreateTaskWorkerFromInvalidExprError extends Message {
  public CannotCreateTaskWorkerFromInvalidExprError() {
    super(FATAL, "Cannot create TaskWorker from InvalidExpr.");
  }
}
