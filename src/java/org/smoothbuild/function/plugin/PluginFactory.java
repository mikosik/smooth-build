package org.smoothbuild.function.plugin;

import static org.smoothbuild.function.plugin.ReflexiveUtils.isPublic;
import static org.smoothbuild.function.plugin.ReflexiveUtils.isStatic;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.plugin.exc.MoreThanOneSmoothFunctionException;
import org.smoothbuild.function.plugin.exc.NoSmoothFunctionException;
import org.smoothbuild.function.plugin.exc.NonPublicSmoothFunctionException;
import org.smoothbuild.function.plugin.exc.NonStaticSmoothFunctionException;
import org.smoothbuild.function.plugin.exc.PluginImplementationException;
import org.smoothbuild.function.plugin.exc.WrongParamsInSmoothFunctionException;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.SmoothFunction;

public class PluginFactory {
  private final PluginSignatureFactory signatureFactory;

  @Inject
  public PluginFactory(PluginSignatureFactory signatureFactory) {
    this.signatureFactory = signatureFactory;
  }

  public Function create(Class<?> klass) throws PluginImplementationException {
    return create(klass, false);
  }

  public Function create(Class<?> klass, boolean builtin) throws PluginImplementationException {
    Method method = getExecuteMethod(klass, builtin);
    Class<?> paramsInterface = method.getParameterTypes()[1];

    Signature signature = signatureFactory.create(method, paramsInterface);
    PluginInvoker invoker = createInvoker(method, paramsInterface);

    return new PluginFunction(signature, invoker);
  }

  private static PluginInvoker createInvoker(Method method, Class<?> paramsInterface)
      throws PluginImplementationException {
    ArgumentsCreator argumentsCreator = new ArgumentsCreator(paramsInterface);
    return new PluginInvoker(method, argumentsCreator);
  }

  private static Method getExecuteMethod(Class<?> klass, boolean builtin)
      throws PluginImplementationException {
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
