package org.smoothbuild.lang.function.nativ;

import static org.smoothbuild.util.ReflexiveUtils.isPublic;
import static org.smoothbuild.util.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import org.smoothbuild.lang.base.NativeApi;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.nativ.err.NativeImplementationException;
import org.smoothbuild.lang.function.nativ.err.NonPublicSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.NonStaticSmoothFunctionException;
import org.smoothbuild.lang.function.nativ.err.WrongParamsInSmoothFunctionException;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class NativeFunctionFactory {
  public static ImmutableList<NativeFunction<?>> createNativeFunctions(HashCode jarHash,
      Class<?> clazz) throws NativeImplementationException {
    Builder<NativeFunction<?>> builder = ImmutableList.builder();
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(SmoothFunction.class)) {
        builder.add(createNativeFunction(jarHash, method));
      }
    }
    return builder.build();
  }

  public static NativeFunction<?> createNativeFunction(HashCode jarHash, Method method) throws
      NativeImplementationException {
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
    if (!(first.equals(NativeApi.class) || first.equals(NativeApiImpl.class))) {
      throw new WrongParamsInSmoothFunctionException(method);
    }

    Class<?> paramsInterface = method.getParameterTypes()[1];
    Signature<? extends Value> signature = SignatureFactory.create(method, paramsInterface);
    return createNativeFunction(jarHash, method, signature, paramsInterface);
  }

  private static <T extends Value> NativeFunction<T> createNativeFunction(HashCode jarHash,
      Method method, Signature<T> signature, Class<?> paramsInterface) throws
      NativeImplementationException {

    /*
     * Cast is safe as T is return type of 'method'.
     */
    @SuppressWarnings("unchecked")
    Invoker<T> invoker = (Invoker<T>) createInvoker(method, paramsInterface);

    return new NativeFunction<>(jarHash, signature, invoker, isCacheable(method));
  }

  private static Invoker<?> createInvoker(Method method, Class<?> paramsInterface) throws
      NativeImplementationException {
    ArgsCreator argsCreator = new ArgsCreator(paramsInterface);
    return new Invoker<>(method, argsCreator);
  }

  private static boolean isCacheable(Method method) {
    return null == method.getAnnotation(NotCacheable.class);
  }
}
