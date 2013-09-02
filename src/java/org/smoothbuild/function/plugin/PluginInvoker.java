package org.smoothbuild.function.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.plugin.Sandbox;

public class PluginInvoker {
  private final Method method;
  private final ArgumentsCreator argumentsCreator;

  public PluginInvoker(Method method, ArgumentsCreator argumentsCreator) {
    this.method = method;
    this.argumentsCreator = argumentsCreator;
  }

  public Object invoke(Sandbox sandbox, Map<String, Object> argumentsMap)
      throws IllegalAccessException, InvocationTargetException {
    Object arguments = argumentsCreator.create(argumentsMap);
    return method.invoke(null, new Object[] { sandbox, arguments });
  }
}
