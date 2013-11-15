package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.util.ReflexiveUtils.isPublic;
import static org.smoothbuild.util.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.exc.MissingNameException;
import org.smoothbuild.lang.function.nativ.exc.MoreThanOneSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.exc.NativeImplementationException;
import org.smoothbuild.lang.function.nativ.exc.NoSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.exc.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.exc.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.exc.WrongParamsInSmoothFunctionException;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.SandboxImpl;

public class NativeFunctionFactory {
  public static NativeFunction create(Class<?> klass, boolean builtin)
      throws NativeImplementationException {
    Method method = getExecuteMethod(klass, builtin);
    Class<?> paramsInterface = method.getParameterTypes()[1];

    Signature signature = SignatureFactory.create(method, paramsInterface);
    Invoker invoker = createInvoker(method, paramsInterface);
    boolean isCacheable = isCacheable(method);

    return new NativeFunction(signature, invoker, isCacheable);
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

  private static boolean isCacheable(Method method) throws MissingNameException {
    SmoothFunction annotation = method.getAnnotation(SmoothFunction.class);
    if (annotation == null) {
      throw new MissingNameException(method);
    }
    return annotation.cacheable();
  }
}
