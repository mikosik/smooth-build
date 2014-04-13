package org.smoothbuild.lang.function.base;

import java.util.Map;

import org.smoothbuild.io.cache.task.TaskResultsDb;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.CachingTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class CachableFunction<T extends SValue> extends AbstractFunction<T> {
  private final TaskResultsDb taskResultsDb;
  private final NativeFunction<T> function;

  public CachableFunction(TaskResultsDb taskResultsDb, NativeFunction<T> function) {
    super(function.signature());
    this.taskResultsDb = taskResultsDb;
    this.function = function;
  }

  @Override
  public Task<T> generateTask(TaskGenerator taskGenerator, Map<String, ? extends Result<?>> args,
      CodeLocation codeLocation) {
    CallHasher<T> callHasher = new CallHasher<T>(function, args);
    Task<T> task = function.generateTask(taskGenerator, args, codeLocation);
    return new CachingTask<T>(taskResultsDb, callHasher, task);
  }
}
