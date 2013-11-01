package org.smoothbuild.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.smoothbuild.db.task.TaskDb;
import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.CachingTask;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.NativeCallHasher;
import org.smoothbuild.task.base.NativeCallTask;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.exec.TaskGenerator;

import com.google.common.collect.ImmutableMap;

/**
 * Function that is implemented completely in java (as opposed to
 * {@link org.smoothbuild.function.def.DefinedFunction} which is defined in
 * Smooth script using Smooth language).
 */
public class NativeFunction extends AbstractFunction {
  private final TaskDb taskDb;
  private final Invoker invoker;

  public NativeFunction(TaskDb taskDb, Signature signature, Invoker invoker) {
    super(signature);
    this.taskDb = checkNotNull(taskDb);
    this.invoker = checkNotNull(invoker);
  }

  @Override
  public LocatedTask generateTask(TaskGenerator taskGenerator, Map<String, Result> args,
      CodeLocation codeLocation) {
    NativeCallTask nativeCallTask = new NativeCallTask(this, args);
    NativeCallHasher nativeCallHasher = new NativeCallHasher(this, args);
    CachingTask task = new CachingTask(taskDb, nativeCallHasher, nativeCallTask);
    return new LocatedTask(task, codeLocation);
  }

  public Value invoke(Sandbox sandbox, ImmutableMap<String, Value> args)
      throws IllegalAccessException, InvocationTargetException {
    return invoker.invoke(sandbox, args);
  }
}
