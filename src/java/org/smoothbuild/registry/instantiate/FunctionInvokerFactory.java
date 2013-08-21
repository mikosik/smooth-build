package org.smoothbuild.registry.instantiate;

import static org.smoothbuild.registry.instantiate.ReflexiveUtils.isPublic;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.lang.type.Files;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.IllegalConstructorParamException;
import org.smoothbuild.registry.exc.MissingConstructorException;
import org.smoothbuild.registry.exc.NonPublicConstructorException;
import org.smoothbuild.registry.exc.TooManyConstructorParamsException;
import org.smoothbuild.registry.exc.TooManyConstructorsException;

public class FunctionInvokerFactory {
  private final InstanceCreatorFactory instanceCreatorFactory;
  private final ReflexiveInvoker reflexiveInvoker;

  @Inject
  public FunctionInvokerFactory(InstanceCreatorFactory instanceCreatorFactory,
      ReflexiveInvoker reflexiveInvoker) {
    this.instanceCreatorFactory = instanceCreatorFactory;
    this.reflexiveInvoker = reflexiveInvoker;
  }

  public FunctionInvoker create(Class<?> klass, Method method, Class<?> paramsInterface)
      throws FunctionImplementationException {
    InstanceCreator instanceCreator = createInstanceCreator(klass);
    ArgumentsCreator argumentsCreator = new ArgumentsCreator(paramsInterface);

    return new FunctionInvoker(reflexiveInvoker, instanceCreator, method, argumentsCreator);
  }

  public InstanceCreator createInstanceCreator(Class<?> klass)
      throws FunctionImplementationException {
    final Constructor<?> constructor = getConstructor(klass);
    Class<?>[] paramTypes = constructor.getParameterTypes();

    if (1 < paramTypes.length) {
      throw new TooManyConstructorParamsException(klass);
    }
    if (paramTypes.length == 0) {
      return instanceCreatorFactory.noArgInstanceCreator(constructor);
    }
    Class<?> paramType = paramTypes[0];
    if (paramType.equals(Files.class)) {
      return instanceCreatorFactory.filesPassingCreator(constructor);
    }
    // TODO disallow FileSystem param in plugin implementations
    if (paramType.equals(FileSystem.class)) {
      return instanceCreatorFactory.fileSystemPassingCreator(constructor);
    }
    // TODO add list with allowed types to exception message
    throw new IllegalConstructorParamException(klass, paramType);
  }

  public static Constructor<?> getConstructor(Class<?> klass)
      throws FunctionImplementationException {
    Constructor<?>[] constructors = klass.getConstructors();
    if (constructors.length == 0) {
      throw new MissingConstructorException(klass);
    }
    if (1 < constructors.length) {
      throw new TooManyConstructorsException(klass);
    }

    Constructor<?> result = constructors[0];
    if (!isPublic(result)) {
      throw new NonPublicConstructorException(klass);
    }
    return result;
  }
}
