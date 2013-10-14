package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.command.CommandLineArguments;
import org.smoothbuild.function.base.Module;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.function.def.DefinedFunction;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.task.exec.err.UnknownFunctionError;

import com.google.common.hash.HashCode;

public class SmoothExecutor {
  private final TaskGenerator taskGenerator;
  private final TaskExecutor taskExecutor;

  @Inject
  public SmoothExecutor(TaskGenerator taskGenerator, TaskExecutor taskExecutor) {
    this.taskGenerator = taskGenerator;
    this.taskExecutor = taskExecutor;
  }

  public void execute(CommandLineArguments args, Module module, MessageListener messages) {
    Name name = args.functionToRun();
    DefinedFunction function = module.getFunction(name);
    if (function == null) {
      messages.report(new UnknownFunctionError(name, module.availableNames()));
      return;
    }
    HashCode hash = taskGenerator.generateTask(function);
    taskExecutor.execute(hash);
  }
}
