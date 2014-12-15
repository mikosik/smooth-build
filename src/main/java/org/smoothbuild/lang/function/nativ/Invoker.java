package org.smoothbuild.lang.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.Value;

public class Invoker {
  private final Method method;
  private final ArgumentsCreator argumentsCreator;

  public Invoker(Method method, ArgumentsCreator argumentsCreator) {
    this.method = checkNotNull(method);
    this.argumentsCreator = checkNotNull(argumentsCreator);
  }

  public Value invoke(NativeApi nativeApi, Map<String, Value> args) throws IllegalAccessException,
  InvocationTargetException {
    Object arguments = argumentsCreator.create(args);
    Object[] javaArguments = new Object[] { nativeApi, arguments };

    return (Value) method.invoke(null, javaArguments);
  }
}
