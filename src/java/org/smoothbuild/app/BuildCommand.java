package org.smoothbuild.app;

import static org.smoothbuild.app.Bootstrapper.bootstrap;
import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.util.List;

@Command(name = "build", description = "Runs build process by executing specified functions.")
public class BuildCommand implements RunnableCommand {
  @Arguments(description = "List of functions to be executed", required = true)
  public List<String> functions;

  @Override
  public boolean runCommand() {
    return bootstrap(BuildWorker.class).run(functions);
  }
}
