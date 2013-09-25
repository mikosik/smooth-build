package org.smoothbuild.command.err;

@SuppressWarnings("serial")
public class IllegalFunctionNameError extends CommandLineError {
  public IllegalFunctionNameError(String functionName) {
    super("Illegal function name = '" + functionName + "'");
  }
}
