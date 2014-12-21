package org.smoothbuild.lang.function.nativ;

import java.lang.reflect.InvocationTargetException;

import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunctionLegacy extends AbstractFunction {
  private final HashCode hash;
  private final Invoker invoker;
  private final boolean isCacheable;

  public NativeFunctionLegacy(Signature signature, Invoker invoker, boolean isCacheable, HashCode hash) {
    super(signature);
    this.hash = hash;
    this.isCacheable = isCacheable;
    this.invoker = invoker;
  }

  public HashCode hash() {
    return hash;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  public Value invoke(NativeApi nativeApi, ImmutableMap<String, Value> args)
      throws IllegalAccessException, InvocationTargetException {
    return invoker.invoke(nativeApi, args);
  }
}
