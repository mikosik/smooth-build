package org.smoothbuild.app;

import static org.smoothbuild.app.Bootstrapper.bootstrap;
import io.airlift.command.Command;

@Command(name = "clean", description = "Removes all cached results.")
public class CleanCommand implements RunnableCommand {
  @Override
  public boolean runCommand() {
    return bootstrap(CleanWorker.class).run();
  }
}
