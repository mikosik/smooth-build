package org.smoothbuild.task.exec;

import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.task.exec.err.NoFunctionSpecifiedError;
import org.smoothbuild.task.exec.err.UnknownFunctionError;

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
      throw new NoFunctionSpecifiedError(module.availableNames());
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
}
