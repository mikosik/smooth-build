package org.smoothbuild.task.exec;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;

public class SmoothExecutor {
  private final ArtifactBuilder artifactBuilder;
  private final Console console;

  @Inject
  public SmoothExecutor(ArtifactBuilder artifactBuilder, Console console) {
    this.artifactBuilder = artifactBuilder;
    this.console = console;
  }

  public void execute(SRuntime runtime, Set<String> names) {
    Functions functions = runtime.functions();
    for (String name : names) {
      if (!functions.contains(name)) {
        console.error("Unknown function '" + name + "'.\n"
            + "Use 'smooth list' to see all available functions.\n");
        return;
      }
      Function function = functions.get(name);
      if (!function.parameters().isEmpty()) {
        console.error(
            "Function '" + name + "' cannot be invoked from command line as it has parameters.\n");
        return;
      }
      artifactBuilder.addArtifact(function);
    }

    artifactBuilder.runBuild();
  }
}
