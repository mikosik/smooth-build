package org.smoothbuild.command;

import org.smoothbuild.function.base.QualifiedName;

public class CommandLineArguments {
  private final String scriptFile;
  private final QualifiedName function;

  public CommandLineArguments(String scriptFile, QualifiedName function) {
    this.scriptFile = scriptFile;
    this.function = function;
  }

  public String scriptFile() {
    return scriptFile;
  }

  public QualifiedName function() {
    return function;
  }
}
