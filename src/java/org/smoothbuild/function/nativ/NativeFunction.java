package org.smoothbuild.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.NativeCallTask;
import org.smoothbuild.task.Task;

import com.google.common.hash.HashCode;

/**
 * Function that is implemented completely in java (as opposed to
 * {@link org.smoothbuild.function.def.DefinedFunction} which is defined in
 * Smooth script using Smooth language).
 */
public class NativeFunction extends AbstractFunction {
  private final Invoker invoker;
  private final HashCode hash;

  public NativeFunction(Signature signature, HashCode hash, Invoker invoker) {
    super(signature);
    this.hash = checkNotNull(hash);
    this.invoker = checkNotNull(invoker);
  }

  public HashCode hash() {
    return hash;
  }

  @Override
  public Task generateTask(Map<String, Task> dependencies, CodeLocation codeLocation) {
    return new NativeCallTask(signature(), codeLocation, invoker, dependencies);
  }
}
