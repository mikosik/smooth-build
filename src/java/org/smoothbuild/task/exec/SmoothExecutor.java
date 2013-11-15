package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Module;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.err.UnknownFunctionError;

public class SmoothExecutor {
  private final ArtifactBuilder artifactBuilder;

  @Inject
  public SmoothExecutor(ArtifactBuilder artifactBuilder) {
    this.artifactBuilder = artifactBuilder;
  }

  public void execute(ExecutionData executionData) {
    CommandLineArguments args = executionData.args();
    Module module = executionData.module();

    for (Name name : args.functionsToRun()) {
      Function function = module.getFunction(name);
      if (function == null) {
        throw new ErrorMessageException(new UnknownFunctionError(name, module.availableNames()));
      }
      artifactBuilder.addArtifact(function);
    }

    artifactBuilder.runBuild();
  }
}
