package org.smoothbuild.lang.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.NativeCallWorker;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Function that is implemented completely in java (as opposed to
 * {@link org.smoothbuild.lang.function.def.DefinedFunction} which is defined in
 * Smooth script using Smooth language).
 */
public class NativeFunction<T extends SValue> extends AbstractFunction<T> {
  private final Invoker<T> invoker;
  private final boolean isCacheable;

  public NativeFunction(Signature<T> signature, Invoker<T> invoker, boolean isCacheable) {
    super(signature);
    this.isCacheable = isCacheable;
    this.invoker = checkNotNull(invoker);
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  @Override
  public ImmutableList<? extends Expr<?>> dependencies(ImmutableMap<String, ? extends Expr<?>> args) {
    return ImmutableList.copyOf(args.values());
  }

  @Override
  public TaskWorker<T> createWorker(ImmutableMap<String, ? extends Expr<?>> args,
      CodeLocation codeLocation) {
    return new NativeCallWorker<T>(this, ImmutableList.copyOf(args.keySet()), codeLocation);
  }

  public T invoke(NativeApi nativeApi, ImmutableMap<String, SValue> args)
      throws IllegalAccessException, InvocationTargetException {
    return invoker.invoke(nativeApi, args);
  }
}
