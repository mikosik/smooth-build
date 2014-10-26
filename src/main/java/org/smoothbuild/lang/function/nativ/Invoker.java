package org.smoothbuild.lang.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.Value;

public class Invoker<T extends Value> {
  private final Method method;
  private final ArgsCreator argsCreator;

  public Invoker(Method method, ArgsCreator argsCreator) {
    this.method = checkNotNull(method);
    this.argsCreator = checkNotNull(argsCreator);
  }

  public T invoke(NativeApi nativeApi, Map<String, Value> args) throws IllegalAccessException,
      InvocationTargetException {
    Object arguments = argsCreator.create(args);
    Object[] javaArguments = new Object[] { nativeApi, arguments };

    @SuppressWarnings("unchecked")
    T result = (T) method.invoke(null, javaArguments);
    return result;
  }
}
