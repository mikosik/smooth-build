package org.smoothbuild.task.exec;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.Ordering;

public class SmoothExecutor {
  private final ArtifactBuilder artifactBuilder;
  private final Console console;
  private final TempManager tempManager;

  @Inject
  public SmoothExecutor(ArtifactBuilder artifactBuilder, Console console, TempManager tempManager) {
    this.artifactBuilder = artifactBuilder;
    this.console = console;
    this.tempManager = tempManager;
  }

  public void execute(Set<Name> functions, Map<Name, Function> module) {
    if (functions.isEmpty()) {
      console.error("No function passed to build command.\n"
          + "  Pass at least one from following available functions:" + indentedNameList(module));
      throw new ExecutionException();
    }
    tempManager.removeTemps();
    for (Name name : functions) {
      Function function = module.get(name);
      if (function == null) {
        console.error("Unknown function " + name + " passed in command line.\n"
            + "  Only following function(s) are available:"
            + indentedNameList(module));
        throw new ExecutionException();
      }
      artifactBuilder.addArtifact(function);
    }

    artifactBuilder.runBuild();
  }

  private String indentedNameList(Map<Name, Function> module) {
    String prefix = "\n    ";
    return prefix + Ordering.usingToString().sortedCopy(module
        .keySet()).stream().map(Name::toString).collect(joining(prefix));
  }
}
