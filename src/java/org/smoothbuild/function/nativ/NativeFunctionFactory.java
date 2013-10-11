package org.smoothbuild.function.nativ;

import static org.smoothbuild.util.ReflexiveUtils.isPublic;
import static org.smoothbuild.util.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.function.base.Function;
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

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;

public class NativeFunctionFactory {
  private final HashFunction hashFunction;

  @Inject
  public NativeFunctionFactory(HashFunction hashFunction) {
    this.hashFunction = hashFunction;
  }

  public Function create(Class<?> klass, boolean builtin) throws NativeImplementationException {
    Method method = getExecuteMethod(klass, builtin);
    Class<?> paramsInterface = method.getParameterTypes()[1];

    Signature signature = SignatureFactory.create(method, paramsInterface);
    HashCode hash = calculateFunctionHash(signature);
    Invoker invoker = createInvoker(method, paramsInterface);

    return new NativeFunction(signature, hash, invoker);
  }

  private HashCode calculateFunctionHash(Signature signature) {
    String name = signature.name().full();
    return hashFunction.hashString(name, Charsets.UTF_8);
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
