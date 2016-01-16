package org.smoothbuild.task.exec;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

public class SmoothExecutor {
  private final Functions functions;
  private final ArtifactBuilder artifactBuilder;
  private final Console console;

  @Inject
  public SmoothExecutor(Functions functions, ArtifactBuilder artifactBuilder, Console console) {
    this.functions = functions;
    this.artifactBuilder = artifactBuilder;
    this.console = console;
  }

  public void execute(Set<Name> names) {
    for (Name name : names) {
      Function function = functions.get(name);
      if (function == null) {
        console.error("Unknown function " + name + ".");
        throw new ExecutionException();
      }
      artifactBuilder.addArtifact(function);
    }

    artifactBuilder.runBuild();
  }
}
