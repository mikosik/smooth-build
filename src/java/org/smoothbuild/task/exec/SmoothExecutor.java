package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.exec.err.UnknownFunctionError;

public class SmoothExecutor {
  private final TaskGraphExecutor taskGraphExecutor;

  @Inject
  public SmoothExecutor(TaskGraphExecutor taskGraphExecutor) {
    this.taskGraphExecutor = taskGraphExecutor;
  }

  public void execute(ExecutionData executionData) {
    CommandLineArguments args = executionData.args();
    Module module = executionData.module();

    Name name = args.functionToRun();
    DefinedFunction function = module.getFunction(name);
    if (function == null) {
      throw new ErrorMessageException(new UnknownFunctionError(name, module.availableNames()));
    }
    taskGraphExecutor.execute(function.generateTask());
  }
}
