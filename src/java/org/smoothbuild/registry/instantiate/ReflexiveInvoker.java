package org.smoothbuild.registry.instantiate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.smoothbuild.registry.exc.CreatingInstanceFailedException;
import org.smoothbuild.registry.exc.InvokingMethodFailedException;

public class ReflexiveInvoker {
  public Object invokeConstructor(Constructor<?> constructor, Object... parameters)
      throws CreatingInstanceFailedException {
    try {
      return constructor.newInstance(parameters);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new CreatingInstanceFailedException(constructor.getDeclaringClass(), e);
    }
  }

  public Object invokeMethod(Object object, Method method, Object... parameters)
      throws InvokingMethodFailedException {
    try {
      return method.invoke(object, parameters);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new InvokingMethodFailedException(method, e);
    }
  }
}
