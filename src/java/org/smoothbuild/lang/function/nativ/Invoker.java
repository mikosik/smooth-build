package org.smoothbuild.lang.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.Value;

public class Invoker {
  private final Method method;
  private final ArgumentsCreator argumentsCreator;

  public Invoker(Method method, ArgumentsCreator argumentsCreator) {
    this.method = checkNotNull(method);
    this.argumentsCreator = checkNotNull(argumentsCreator);
  }

  public Value invoke(Sandbox sandbox, Map<String, Value> args) throws IllegalAccessException,
      InvocationTargetException {
    Object arguments = argumentsCreator.create(args);
    return (Value) method.invoke(null, new Object[] { sandbox, arguments });
  }
}
