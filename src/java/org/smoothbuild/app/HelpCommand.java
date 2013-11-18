package org.smoothbuild.app;

import io.airlift.command.Help;

public class HelpCommand extends Help implements RunnableCommand {
  @Override
  public boolean runCommand() {
    run();
    return true;
  }
}
