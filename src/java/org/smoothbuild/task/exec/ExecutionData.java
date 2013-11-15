package org.smoothbuild.task.exec;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.lang.function.base.Module;

public class ExecutionData {
  private final CommandLineArguments args;
  private final Module module;

  public ExecutionData(CommandLineArguments args, Module module) {
    this.args = args;
    this.module = module;
  }

  public CommandLineArguments args() {
    return args;
  }

  public Module module() {
    return module;
  }
}
