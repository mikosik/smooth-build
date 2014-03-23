package org.smoothbuild.command.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class IllegalFunctionNameError extends Message {
  public IllegalFunctionNameError(String functionName) {
    super(ERROR, "Illegal function name '" + functionName + "' passed in command line.");
  }
}
