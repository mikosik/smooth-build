package org.smoothbuild.function.plugin;

import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.function.plugin.exc.FunctionReflectionException;
import org.smoothbuild.plugin.Sandbox;

public class PluginInvoker {
  private final ReflexiveInvoker reflexiveInvoker;
  private final Method method;
  private final ArgumentsCreator argumentsCreator;

  public PluginInvoker(ReflexiveInvoker reflexiveInvoker, Method method,
      ArgumentsCreator argumentsCreator) {
    this.reflexiveInvoker = reflexiveInvoker;
    this.method = method;
    this.argumentsCreator = argumentsCreator;
  }

  public Object invoke(Sandbox sandbox, Map<String, Object> argumentsMap)
      throws FunctionReflectionException {
    Object arguments = argumentsCreator.create(argumentsMap);
    return reflexiveInvoker.invokeMethod(null, method, sandbox, arguments);
  }
}
