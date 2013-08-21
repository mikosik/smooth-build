package org.smoothbuild.registry.instantiate;

import java.lang.reflect.Method;

import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.FunctionReflectionException;

import com.google.common.collect.ImmutableMap;

public class FunctionInvoker {
  private final ReflexiveInvoker reflexiveInvoker;
  private final InstanceCreator instanceCreator;
  private final Method executeMethod;
  private final ArgumentsCreator argumentsCreator;

  public FunctionInvoker(ReflexiveInvoker reflexiveInvoker, InstanceCreator instanceCreator,
      Method executeMethod, ArgumentsCreator argumentsCreator) {
    this.reflexiveInvoker = reflexiveInvoker;
    this.instanceCreator = instanceCreator;
    this.executeMethod = executeMethod;
    this.argumentsCreator = argumentsCreator;
  }

  public Object invoke(Path resultDir, ImmutableMap<String, Object> argumentsMap)
      throws FunctionReflectionException {
    Object object = instanceCreator.createInstance(resultDir);
    Object arguments = argumentsCreator.create(argumentsMap);
    return reflexiveInvoker.invokeMethod(object, executeMethod, arguments);
  }
}
