package org.smoothbuild.function.nativ;

import static org.smoothbuild.util.ReflexiveUtils.isPublic;
import static org.smoothbuild.util.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.nativ.exc.MoreThanOneSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NativeImplementationException;
import org.smoothbuild.function.nativ.exc.NoSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NonPublicSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NonStaticSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.WrongParamsInSmoothFunctionException;
import org.smoothbuild.object.ResultCache;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.task.exec.SandboxImpl;

public class NativeFunctionFactory {
  private final ResultCache resultCache;

  @Inject
  public NativeFunctionFactory(ResultCache resultCache) {
    this.resultCache = resultCache;
  }

  public NativeFunction create(Class<?> klass, boolean builtin)
      throws NativeImplementationException {
    Method method = getExecuteMethod(klass, builtin);
    Class<?> paramsInterface = method.getParameterTypes()[1];

    Signature signature = SignatureFactory.create(method, paramsInterface);
    Invoker invoker = createInvoker(method, paramsInterface);

    return new NativeFunction(resultCache, signature, invoker);
  }

  private static Invoker createInvoker(Method method, Class<?> paramsInterface)
      throws NativeImplementationException {
    ArgumentsCreator argumentsCreator = new ArgumentsCreator(paramsInterface);
    return new Invoker(method, argumentsCreator);
  }

  private static Method getExecuteMethod(Class<?> klass, boolean builtin)
      throws NativeImplementationException {
    Class<SmoothFunction> executeAnnotation = SmoothFunction.class;
    Method result = null;
    for (Method method : klass.getDeclaredMethods()) {
      if (method.isAnnotationPresent(executeAnnotation)) {
        if (!isPublic(method)) {
          throw new NonPublicSmoothFunctionException(method);
        }
        if (!isStatic(method)) {
          throw new NonStaticSmoothFunctionException(method);
        }
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 2) {
          throw new WrongParamsInSmoothFunctionException(method);
        }
        Class<?> first = paramTypes[0];
        if (!(first.equals(Sandbox.class) || (builtin && first.equals(SandboxImpl.class)))) {
          throw new WrongParamsInSmoothFunctionException(method);
        }

        if (result == null) {
          result = method;
        } else {
          throw new MoreThanOneSmoothFunctionException(klass);
        }
      }
    }
    if (result == null) {
      throw new NoSmoothFunctionException(klass);
    }
    return result;
  }
}
