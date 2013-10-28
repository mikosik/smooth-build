package org.smoothbuild.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.object.ResultDb;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.NativeCallTask;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableMap;

/**
 * Function that is implemented completely in java (as opposed to
 * {@link org.smoothbuild.function.def.DefinedFunction} which is defined in
 * Smooth script using Smooth language).
 */
public class NativeFunction extends AbstractFunction {
  private final ResultDb resultDb;
  private final Invoker invoker;

  public NativeFunction(ResultDb resultDb, Signature signature, Invoker invoker) {
    super(signature);
    this.resultDb = checkNotNull(resultDb);
    this.invoker = checkNotNull(invoker);
  }

  @Override
  public Task generateTask(Map<String, Task> args, CodeLocation codeLocation) {
    return new NativeCallTask(resultDb, this, codeLocation, args);
  }

  public Value invoke(Sandbox sandbox, ImmutableMap<String, Value> args)
      throws IllegalAccessException, InvocationTargetException {
    return invoker.invoke(sandbox, args);
  }
}
