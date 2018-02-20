package org.smoothbuild.task.exec;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;

public class SmoothExecutor {
  private final SRuntime runtime;
  private final ArtifactBuilder artifactBuilder;
  private final Console console;

  @Inject
  public SmoothExecutor(SRuntime runtime, ArtifactBuilder artifactBuilder, Console console) {
    this.runtime = runtime;
    this.artifactBuilder = artifactBuilder;
    this.console = console;
  }

  public void execute(Set<String> names) {
    Functions functions = runtime.functions();
    for (String name : names) {
      if (!functions.contains(name)) {
        console.error("Unknown function '" + name + "'.\n"
            + "Use 'smooth list' to see all available functions.\n");
        return;
      }
      artifactBuilder.addArtifact(functions.get(name));
    }

    artifactBuilder.runBuild();
  }
}
