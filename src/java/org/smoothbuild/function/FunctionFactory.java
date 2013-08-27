package org.smoothbuild.function;

import static org.smoothbuild.function.ReflexiveUtils.isPublic;
import static org.smoothbuild.function.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.function.exc.FunctionImplementationException;
import org.smoothbuild.function.exc.MoreThanOneExecuteMethodException;
import org.smoothbuild.function.exc.NoExecuteMethodException;
import org.smoothbuild.function.exc.NonPublicExecuteMethodException;
import org.smoothbuild.function.exc.PluginImplementationException;
import org.smoothbuild.function.exc.StaticExecuteMethodException;
import org.smoothbuild.function.exc.TooManyParamsInExecuteMethodException;
import org.smoothbuild.function.exc.ZeroParamsInExecuteMethodException;
import org.smoothbuild.plugin.ExecuteMethod;

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

    return new PluginFunction(signature, invoker);
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
      throw new ZeroParamsInExecuteMethodException(executeMethod.getDeclaringClass());
    }
    if (1 < types.length) {
      throw new TooManyParamsInExecuteMethodException(executeMethod.getDeclaringClass());
    }
    return types[0];
  }
}
