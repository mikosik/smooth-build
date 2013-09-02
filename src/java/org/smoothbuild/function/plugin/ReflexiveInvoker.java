package org.smoothbuild.function.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.smoothbuild.function.plugin.exc.InvokingMethodFailedException;

public class ReflexiveInvoker {
  public static Object invokeMethod(Object object, Method method, Object... parameters)
      throws InvokingMethodFailedException {
    try {
      return method.invoke(object, parameters);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new InvokingMethodFailedException(method, e);
    }
  }
}
