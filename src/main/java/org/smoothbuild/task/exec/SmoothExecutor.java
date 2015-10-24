package org.smoothbuild.task.exec;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.task.exec.err.UnknownFunctionError;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

public class SmoothExecutor {
  private final ArtifactBuilder artifactBuilder;

  @Inject
  public SmoothExecutor(ArtifactBuilder artifactBuilder) {
    this.artifactBuilder = artifactBuilder;
  }

  public void execute(ExecutionData executionData) {
    Set<Name> functions = executionData.functions();
    Module module = executionData.module();

    if (functions.isEmpty()) {
      throw new ExecutionException("error: No function passed to build command.\n"
          + "  Pass at least one from following available functions:" + indentedNameList(module));
    }
    for (Name name : executionData.functions()) {
      Function function = module.getFunction(name);
      if (function == null) {
        throw new UnknownFunctionError(name, module.availableNames());
      }
      artifactBuilder.addArtifact(function);
    }

    artifactBuilder.runBuild();
  }

  private String indentedNameList(Module module) {
    String prefix = "\n    ";
    List<Name> sortedNames = Ordering.usingToString().sortedCopy(module
        .availableNames());
    return prefix + Joiner.on(prefix).join(sortedNames);
  }
}
