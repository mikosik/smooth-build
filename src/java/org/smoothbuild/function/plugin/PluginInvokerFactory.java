package org.smoothbuild.function.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.function.plugin.exc.FunctionImplementationException;
import org.smoothbuild.function.plugin.exc.IllegalConstructorParamException;
import org.smoothbuild.function.plugin.exc.MissingConstructorException;
import org.smoothbuild.function.plugin.exc.TooManyConstructorParamsException;
import org.smoothbuild.function.plugin.exc.TooManyConstructorsException;
import org.smoothbuild.plugin.Files;

import com.google.common.collect.ImmutableList;

public class PluginInvokerFactory {
  private final InstanceCreatorFactory instanceCreatorFactory;
  private final ReflexiveInvoker reflexiveInvoker;

  @Inject
  public PluginInvokerFactory(InstanceCreatorFactory instanceCreatorFactory,
      ReflexiveInvoker reflexiveInvoker) {
    this.instanceCreatorFactory = instanceCreatorFactory;
    this.reflexiveInvoker = reflexiveInvoker;
  }

  public PluginInvoker create(Class<?> klass, Method method, Class<?> paramsInterface,
      boolean builtin) throws FunctionImplementationException {
    InstanceCreator instanceCreator = createInstanceCreator(klass, builtin);
    ArgumentsCreator argumentsCreator = new ArgumentsCreator(paramsInterface);

    return new PluginInvoker(reflexiveInvoker, instanceCreator, method, argumentsCreator);
  }

  public InstanceCreator createInstanceCreator(Class<?> klass, boolean builtin)
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
    if (builtin) {
      if (paramType.equals(FileSystem.class)) {
        return instanceCreatorFactory.fileSystemPassingCreator(constructor);
      }
    }
    throw new IllegalConstructorParamException(klass, paramType, allowedTypes(builtin));
  }

  private static ImmutableList<String> allowedTypes(boolean builtin) {
    String filesName = Files.class.getCanonicalName();
    if (builtin) {
      String fileSystemName = FileSystem.class.getCanonicalName();
      return ImmutableList.of(fileSystemName, filesName);
    } else {
      return ImmutableList.of(filesName);
    }
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

    return constructors[0];
  }
}
