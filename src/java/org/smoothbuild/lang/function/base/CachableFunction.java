package org.smoothbuild.lang.function.base;

import java.util.Map;

import org.smoothbuild.io.cache.task.TaskDb;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.CachingTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class CachableFunction<T extends SValue> extends AbstractFunction<T> {
  private final TaskDb taskDb;
  private final NativeFunction<T> function;

  public CachableFunction(TaskDb taskDb, NativeFunction<T> function) {
    super(function.signature());
    this.taskDb = taskDb;
    this.function = function;
  }

  @Override
  public Task<T> generateTask(TaskGenerator taskGenerator, Map<String, ? extends Result<?>> args,
      CodeLocation codeLocation) {
    CallHasher<T> callHasher = new CallHasher<T>(function, args);
    Task<T> task = function.generateTask(taskGenerator, args, codeLocation);
    return new CachingTask<T>(taskDb, callHasher, task);
  }
}
