package org.smoothbuild.exec.run;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.exec.run.artifact.ArtifactBuilder;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;

public class BuildRunner {
  private final ArtifactBuilder artifactBuilder;
  private final Console console;

  @Inject
  public BuildRunner(ArtifactBuilder artifactBuilder, Console console) {
    this.artifactBuilder = artifactBuilder;
    this.console = console;
  }

  public void execute(SRuntime runtime, Set<String> names) {
    Functions functions = runtime.functions();
    List<Function> functionsToRun = new ArrayList<>();
    for (String name : names) {
      if (!functions.contains(name)) {
        console.error("Unknown function '" + name + "'.\n"
            + "Try 'smooth list' to see all available functions.\n");
        return;
      }
      Function function = functions.get(name);
      if (!function.canBeCalledArgless()) {
        console.error("Function '" + name
            + "' cannot be invoked from command line as it requires arguments.\n");
        return;
      }
      functionsToRun.add(function);
    }

    artifactBuilder.buildArtifacts(functionsToRun);
  }
}
