package org.smoothbuild.registry.instantiate;

import java.lang.reflect.Constructor;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.type.FilesRw;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.IllegalConstructorParamException;
import org.smoothbuild.registry.exc.MissingConstructorException;
import org.smoothbuild.registry.exc.TooManyConstructorParamsException;
import org.smoothbuild.registry.exc.TooManyConstructorsException;

public class InstantiatorFactory {
  private final InstantiatorFactoryRaw factoryRaw;

  public InstantiatorFactory(InstantiatorFactoryRaw factoryRaw) {
    this.factoryRaw = factoryRaw;
  }

  public Instantiator create(Class<? extends Function> klass)
      throws FunctionImplementationException {
    final Constructor<? extends Function> constructor = getConstructor(klass);
    Class<?>[] paramTypes = constructor.getParameterTypes();
    if (1 < paramTypes.length) {
      throw new TooManyConstructorParamsException(klass);
    }
    if (paramTypes.length == 0) {
      return factoryRaw.noArg(constructor);
    }
    Class<?> paramType = paramTypes[0];
    if (paramType.equals(FilesRw.class)) {
      return factoryRaw.filesRwInstantiator(constructor);
    }
    // TODO disallow FileSystem param in plugin implementations
    if (paramType.equals(FileSystem.class)) {
      return factoryRaw.fileSystemInstantiator(constructor);
    }
    // TODO add list with allowed types to exception message
    throw new IllegalConstructorParamException(klass, paramType);
  }

  public static Constructor<? extends Function> getConstructor(Class<? extends Function> klass)
      throws MissingConstructorException, TooManyConstructorsException {
    Constructor<?>[] constructors = klass.getConstructors();
    if (constructors.length == 0) {
      throw new MissingConstructorException(klass);
    }
    if (1 < constructors.length) {
      throw new TooManyConstructorsException(klass);
    }

    @SuppressWarnings("unchecked")
    Constructor<? extends Function> result = (Constructor<? extends Function>) constructors[0];
    return result;
  }
}
