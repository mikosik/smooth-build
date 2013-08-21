package org.smoothbuild.function;

import java.lang.reflect.Method;

import org.smoothbuild.function.exc.FunctionReflectionException;
import org.smoothbuild.lang.type.Path;

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
