package org.smoothbuild.function.plugin;

import java.lang.reflect.Method;
import java.util.Map;

import org.smoothbuild.function.plugin.exc.FunctionReflectionException;
import org.smoothbuild.plugin.Path;

public class PluginInvoker {
  private final ReflexiveInvoker reflexiveInvoker;
  private final InstanceCreator instanceCreator;
  private final Method executeMethod;
  private final ArgumentsCreator argumentsCreator;

  public PluginInvoker(ReflexiveInvoker reflexiveInvoker, InstanceCreator instanceCreator,
      Method executeMethod, ArgumentsCreator argumentsCreator) {
    this.reflexiveInvoker = reflexiveInvoker;
    this.instanceCreator = instanceCreator;
    this.executeMethod = executeMethod;
    this.argumentsCreator = argumentsCreator;
  }

  public Object invoke(Path resultDir, Map<String, Object> argumentsMap)
      throws FunctionReflectionException {
    Object object = instanceCreator.createInstance(resultDir);
    Object arguments = argumentsCreator.create(argumentsMap);
    return reflexiveInvoker.invokeMethod(object, executeMethod, arguments);
  }
}
