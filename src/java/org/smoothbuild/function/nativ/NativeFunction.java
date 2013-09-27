package org.smoothbuild.function.nativ;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.task.InvokeTask;
import org.smoothbuild.task.Task;

/**
 * Function that is implemented completely in java (as opposed to
 * {@link org.smoothbuild.function.def.DefinedFunction} which is defined in
 * Smooth script using Smooth language).
 */
public class NativeFunction extends AbstractFunction {
  private final Invoker invoker;

  public NativeFunction(Signature signature, Invoker invoker) {
    super(signature);
    this.invoker = invoker;
  }

  @Override
  public Task generateTask(Map<String, Task> dependencies) {
    return new InvokeTask(signature(), invoker, dependencies);
  }
}
