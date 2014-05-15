package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.util.ReflexiveUtils.isPublic;
import static org.smoothbuild.util.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.err.MissingNameException;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.lang.function.nativ.err.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.WrongParamsInSmoothFunctionException;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.NativeApiImpl;

public class NativeFunctionFactory {

  public static NativeFunction<?> createNativeFunction(Method method, boolean builtin)
      throws NativeImplementationException, MissingNameException {
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
    if (!(first.equals(NativeApi.class) || (builtin && first.equals(NativeApiImpl.class)))) {
      throw new WrongParamsInSmoothFunctionException(method);
    }

    Class<?> paramsInterface = method.getParameterTypes()[1];
    Signature<? extends SValue> signature = SignatureFactory.create(method, paramsInterface);
    return createNativeFunction(method, signature, paramsInterface);
  }

  private static <T extends SValue> NativeFunction<T> createNativeFunction(Method method,
      Signature<T> signature, Class<?> paramsInterface) throws NativeImplementationException,
      MissingNameException {

    /*
     * Cast is safe as T is return type of 'method'.
     */
    @SuppressWarnings("unchecked")
    Invoker<T> invoker = (Invoker<T>) createInvoker(method, paramsInterface);

    return new NativeFunction<>(signature, invoker, isCacheable(method));
  }

  private static Invoker<?> createInvoker(Method method, Class<?> paramsInterface)
      throws NativeImplementationException {
    ArgsCreator argsCreator = new ArgsCreator(paramsInterface);
    return new Invoker<>(method, argsCreator);
  }

  private static boolean isCacheable(Method method) throws MissingNameException {
    SmoothFunction annotation = method.getAnnotation(SmoothFunction.class);
    if (annotation == null) {
      throw new MissingNameException(method);
    }
    return annotation.cacheable();
  }
}
