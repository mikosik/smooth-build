package org.smoothbuild.command;

import org.smoothbuild.function.base.Name;

public class CommandLineArguments {
  private final String scriptFile;
  private final Name function;

  public CommandLineArguments(String scriptFile, Name function) {
    this.scriptFile = scriptFile;
    this.function = function;
  }

  public String scriptFile() {
    return scriptFile;
  }

  public Name functionToRun() {
    return function;
  }
}
