package org.smoothbuild.function.nativ;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.object.Hashed;
import org.smoothbuild.plugin.Sandbox;

public class Invoker {
  private final Method method;
  private final ArgumentsCreator argumentsCreator;

  public Invoker(Method method, ArgumentsCreator argumentsCreator) {
    this.method = checkNotNull(method);
    this.argumentsCreator = checkNotNull(argumentsCreator);
  }

  public Hashed invoke(Sandbox sandbox, Map<String, Hashed> args) throws IllegalAccessException,
      InvocationTargetException {
    Object arguments = argumentsCreator.create(args);
    return (Hashed) method.invoke(null, new Object[] { sandbox, arguments });
  }
}
