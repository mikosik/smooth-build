package org.smoothbuild.command;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Name;

public class CommandLineArguments {
  private final String scriptFile;
  private final Name function;

  public CommandLineArguments(String scriptFile, Name function) {
    this.scriptFile = checkNotNull(scriptFile);
    this.function = checkNotNull(function);
  }

  public String scriptFile() {
    return scriptFile;
  }

  public Name functionToRun() {
    return function;
  }
}
