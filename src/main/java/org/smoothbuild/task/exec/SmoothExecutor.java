package org.smoothbuild.task.exec;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.Ordering;

public class SmoothExecutor {
  private final ArtifactBuilder artifactBuilder;
  private final Console console;

  @Inject
  public SmoothExecutor(ArtifactBuilder artifactBuilder, Console console) {
    this.artifactBuilder = artifactBuilder;
    this.console = console;
  }

  public void execute(Set<Name> names, Map<Name, Function> functions) {
    for (Name name : names) {
      Function function = functions.get(name);
      if (function == null) {
        console.error("Unknown function " + name + " passed in command line.\n"
            + "  Only following function(s) are available:" + indentedNameList(functions));
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
