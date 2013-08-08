package org.smoothbuild.registry.instantiate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.smoothbuild.lang.function.Function;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public class ConstructorInvoker {
  public Function invoke(Constructor<? extends Function> constructor,
      Object... parameters) throws CreatingInstanceFailedException {
    try {
      return constructor.newInstance(parameters);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new CreatingInstanceFailedException(constructor.getDeclaringClass(), e);
    }
  }
}
