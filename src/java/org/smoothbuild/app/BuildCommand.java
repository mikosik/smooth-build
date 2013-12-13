package org.smoothbuild.app;

import static org.smoothbuild.app.Bootstrapper.bootstrap;
import io.airlift.command.Arguments;
import io.airlift.command.Command;

import java.util.List;

import com.google.common.collect.ImmutableList;

@Command(name = "build", description = "Runs build process by executing specified functions.")
public class BuildCommand implements RunnableCommand {
  @Arguments(description = "List of functions to be executed", required = false)
  public List<String> functions;

  @Override
  public boolean runCommand() {
    if (functions == null) {
      functions = ImmutableList.of();
    }
    return bootstrap(BuildWorker.class).run(functions);
  }
}
