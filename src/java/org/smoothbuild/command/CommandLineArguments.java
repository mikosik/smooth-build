package org.smoothbuild.command;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.function.base.Name;

public class CommandLineArguments {
  private final Path scriptFile;
  private final Name function;

  public CommandLineArguments(Path scriptFile, Name function) {
    this.scriptFile = checkNotNull(scriptFile);
    this.function = checkNotNull(function);
  }

  public Path scriptFile() {
    return scriptFile;
  }

  public Name functionToRun() {
    return function;
  }
}
