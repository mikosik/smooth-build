package org.smoothbuild.lang.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.NativeCallWorker;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction<T extends SValue> extends AbstractFunction<T> {
  private final HashCode hash;
  private final Invoker<T> invoker;
  private final boolean isCacheable;

  public NativeFunction(HashCode jarHash, Signature<T> signature, Invoker<T> invoker,
      boolean isCacheable) {
    super(signature);
    this.hash = functionHash(jarHash, signature);
    this.isCacheable = isCacheable;
    this.invoker = checkNotNull(invoker);
  }

  private static HashCode functionHash(HashCode jarHash, Signature<?> signature) {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(jarHash.asBytes());
    hasher.putString(signature.name().value(), SmoothConstants.CHARSET);
    return hasher.hash();
  }

  public HashCode hash() {
    return hash;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  @Override
  public ImmutableList<? extends Expr<?>> dependencies(
      ImmutableMap<String, ? extends Expr<?>> args) {
    return ImmutableList.copyOf(args.values());
  }

  @Override
  public TaskWorker<T> createWorker(ImmutableMap<String, ? extends Expr<?>> args,
      boolean isInternal, CodeLocation codeLocation) {
    return new NativeCallWorker<>(this, ImmutableList.copyOf(args.keySet()), isInternal,
        codeLocation);
  }

  public T invoke(NativeApi nativeApi, ImmutableMap<String, SValue> args) throws
      IllegalAccessException, InvocationTargetException {
    return invoker.invoke(nativeApi, args);
  }
}
