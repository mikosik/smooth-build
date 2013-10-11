package org.smoothbuild.function.nativ;

import static org.smoothbuild.util.ReflexiveUtils.isPublic;
import static org.smoothbuild.util.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.nativ.exc.MoreThanOneSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NativeImplementationException;
import org.smoothbuild.function.nativ.exc.NoSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NonPublicSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.NonStaticSmoothFunctionException;
import org.smoothbuild.function.nativ.exc.WrongParamsInSmoothFunctionException;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;
import org.smoothbuild.task.SandboxImpl;
import org.smoothbuild.util.Hash;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;

public class NativeFunctionFactory {

  public static NativeFunction create(Class<?> klass, boolean builtin)
      throws NativeImplementationException {
    Method method = getExecuteMethod(klass, builtin);
    Class<?> paramsInterface = method.getParameterTypes()[1];

    Signature signature = SignatureFactory.create(method, paramsInterface);
    HashCode hash = calculateFunctionHash(signature);
    Invoker invoker = createInvoker(method, paramsInterface);

    return new NativeFunction(signature, hash, invoker);
  }

  private static HashCode calculateFunctionHash(Signature signature) {
    String name = signature.name().full();
    return Hash.hashFunction().hashString(name, Charsets.UTF_8);
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
