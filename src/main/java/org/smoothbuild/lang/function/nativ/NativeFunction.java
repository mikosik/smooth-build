package org.smoothbuild.lang.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.AbstractFunction;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction<T extends Value> extends AbstractFunction<T> {
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

  public T invoke(NativeApi nativeApi, ImmutableMap<String, Value> args)
      throws IllegalAccessException, InvocationTargetException {
    return invoker.invoke(nativeApi, args);
  }
}
