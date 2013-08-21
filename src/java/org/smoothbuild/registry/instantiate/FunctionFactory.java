package org.smoothbuild.registry.instantiate;

import static org.smoothbuild.registry.instantiate.ReflexiveUtils.isPublic;
import static org.smoothbuild.registry.instantiate.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.lang.function.ExecuteMethod;
import org.smoothbuild.registry.exc.FunctionImplementationException;
import org.smoothbuild.registry.exc.MoreThanOneExecuteMethodException;
import org.smoothbuild.registry.exc.NoExecuteMethodException;
import org.smoothbuild.registry.exc.NonPublicExecuteMethodException;
import org.smoothbuild.registry.exc.PluginImplementationException;
import org.smoothbuild.registry.exc.StaticExecuteMethodException;
import org.smoothbuild.registry.exc.TooManyParamsInExecuteMethodException;
import org.smoothbuild.registry.exc.ZeroParamsInExecuteMethodException;

public class FunctionFactory {
  private final FunctionSignatureFactory signatureFactory;
  private final FunctionInvokerFactory invokerFactory;

  @Inject
  public FunctionFactory(FunctionSignatureFactory signatureFactory,
      FunctionInvokerFactory invokerFactory) {
    this.signatureFactory = signatureFactory;
    this.invokerFactory = invokerFactory;
  }

  public Function create(Class<?> klass) throws PluginImplementationException {
    Method method = getExecuteMethod(klass);
    Class<?> paramsInterface = getParamsInterface(method);

    FunctionSignature signature = signatureFactory.create(klass, method, paramsInterface);
    FunctionInvoker invoker = invokerFactory.create(klass, method, paramsInterface);

    return new Function(signature, invoker);
  }

  private static Method getExecuteMethod(Class<?> klass) throws FunctionImplementationException {
    Class<ExecuteMethod> executeAnnotation = ExecuteMethod.class;
    Method result = null;
    for (Method method : klass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(executeAnnotation)) {
        if (!isPublic(method)) {
          throw new NonPublicExecuteMethodException(klass, method);
        }
        if (isStatic(method)) {
          throw new StaticExecuteMethodException(klass, method);
        }
        if (result == null) {
          result = method;
        } else {
          throw new MoreThanOneExecuteMethodException(klass);
        }
      }
    }
    if (result == null) {
      throw new NoExecuteMethodException(klass);
    }
    return result;
  }

  private static Class<?> getParamsInterface(Method executeMethod)
      throws FunctionImplementationException {
    Class<?>[] types = executeMethod.getParameterTypes();
    if (types.length == 0) {
      throw new TooManyParamsInExecuteMethodException(executeMethod.getDeclaringClass());
    }
    if (1 < types.length) {
      throw new ZeroParamsInExecuteMethodException(executeMethod.getDeclaringClass());
    }
    return types[0];
  }
}
