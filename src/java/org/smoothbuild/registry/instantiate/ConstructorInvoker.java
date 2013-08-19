package org.smoothbuild.registry.instantiate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.exc.CreatingInstanceFailedException;

public class ConstructorInvoker {
  public FunctionDefinition invoke(Constructor<? extends FunctionDefinition> constructor,
      Object... parameters) throws CreatingInstanceFailedException {
    try {
      return constructor.newInstance(parameters);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      throw new CreatingInstanceFailedException(constructor.getDeclaringClass(), e);
    }
  }
}
