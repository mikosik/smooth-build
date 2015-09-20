package org.smoothbuild.lang.expr.err;

import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.message.base.Message;

public class CannotCreateComputerFromInvalidExpressionError extends Message {
  public CannotCreateComputerFromInvalidExpressionError() {
    super(FATAL, "Cannot create Computer from InvalidExpression.");
  }
}
