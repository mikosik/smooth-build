package org.smoothbuild.lang.function.base;

import java.util.Map;

import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.CachingTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class CachableFunction extends AbstractFunction {
  private final TaskDb taskDb;
  private final Function function;

  public CachableFunction(TaskDb taskDb, Function function) {
    super(function.signature());
    this.taskDb = taskDb;
    this.function = function;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator, Map<String, Result> args,
      CodeLocation codeLocation) {
    CallHasher callHasher = new CallHasher(function, args);
    Task task = function.generateTask(taskGenerator, args, codeLocation);
    return new CachingTask(taskDb, callHasher, task);
  }
}
