package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;
import org.smoothbuild.task.exec.err.UnknownFunctionError;
import org.smoothbuild.util.Empty;

public class SmoothExecutor {
  private final TaskGenerator taskGenerator;

  @Inject
  public SmoothExecutor(TaskGenerator taskGenerator) {
    this.taskGenerator = taskGenerator;
  }

  public void execute(ExecutionData executionData) {
    CommandLineArguments args = executionData.args();
    Module module = executionData.module();

    Name name = args.functionToRun();
    Function function = module.getFunction(name);
    if (function == null) {
      throw new ErrorMessageException(new UnknownFunctionError(name, module.availableNames()));
    }
    Result result = taskGenerator.generateTask(new TaskableCall(function));
    try {
      result.result();
    } catch (BuildInterruptedException e) {
      // Nothing to do. Just quit the build process.
    }
  }

  private static class TaskableCall implements Taskable {
    private final Function function;

    public TaskableCall(Function function) {
      this.function = function;
    }

    @Override
    public Task generateTask(TaskGenerator taskGenerator) {
      CodeLocation ignoredCodeLocation = null;
      return function.generateTask(taskGenerator, Empty.stringTaskResultMap(), ignoredCodeLocation);
    }
  }
}
