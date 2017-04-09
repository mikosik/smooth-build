package org.smoothbuild.task.exec;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;

public class SmoothExecutor {
  private final ArtifactBuilder artifactBuilder;
  private final Console console;

  @Inject
  public SmoothExecutor(ArtifactBuilder artifactBuilder, Console console) {
    this.artifactBuilder = artifactBuilder;
    this.console = console;
  }

  public void execute(Functions functions, Set<Name> names) {
    for (Name name : names) {
      if (!functions.contains(name)) {
        console.error("Unknown function " + name + ".");
        throw new ExecutionException();
      }
      artifactBuilder.addArtifact(functions.get(name));
    }

    artifactBuilder.runBuild();
  }
}
