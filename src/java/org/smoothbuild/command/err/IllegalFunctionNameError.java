package org.smoothbuild.command.err;

public class IllegalFunctionNameError extends CommandLineError {
  public IllegalFunctionNameError(String functionName) {
    super("Illegal function name = '" + functionName + "'");
  }
}
