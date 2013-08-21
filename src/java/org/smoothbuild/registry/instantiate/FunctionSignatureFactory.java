package org.smoothbuild.registry.instantiate;

import static org.smoothbuild.lang.function.FullyQualifiedName.fullyQualifiedName;

import java.lang.reflect.Method;

import org.smoothbuild.lang.function.FullyQualifiedName;
import org.smoothbuild.lang.function.FunctionName;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.registry.exc.ForbiddenParamTypeException;
import org.smoothbuild.registry.exc.IllegalFunctionNameException;
import org.smoothbuild.registry.exc.IllegalReturnTypeException;
import org.smoothbuild.registry.exc.MissingNameException;
import org.smoothbuild.registry.exc.ParamMethodHasArgumentsException;
import org.smoothbuild.registry.exc.ParamsIsNotInterfaceException;
import org.smoothbuild.registry.exc.PluginImplementationException;

public class FunctionSignatureFactory {

  public FunctionSignature create(Class<?> klass, Method method, Class<?> paramsInterface)
      throws PluginImplementationException {
    Type type = getReturnType(method);
    FullyQualifiedName name = getFunctionName(klass);
    Params params = getParams(klass, method, paramsInterface);

    return new FunctionSignature(type, name, params);
  }

  private static FullyQualifiedName getFunctionName(Class<?> klass)
      throws PluginImplementationException {
    FunctionName annotation = klass.getAnnotation(FunctionName.class);
    if (annotation == null) {
      throw new MissingNameException(klass);
    }
    try {
      return fullyQualifiedName(annotation.value());
    } catch (IllegalArgumentException e) {
      throw new IllegalFunctionNameException(klass, e.getMessage());
    }
  }

  private static Type getReturnType(Method method) throws PluginImplementationException {
    Class<?> javaType = method.getReturnType();
    Type type = Type.toType(javaType);
    if (type == null) {
      throw new IllegalReturnTypeException(method.getDeclaringClass(), javaType);
    }
    return type;
  }

  private static Params getParams(Class<?> klass, Method method, Class<?> paramsInterface)
      throws PluginImplementationException {
    if (!paramsInterface.isInterface()) {
      throw new ParamsIsNotInterfaceException(klass, method);
    }
    Method[] methods = paramsInterface.getMethods();
    Param[] params = new Param[methods.length];
    for (int i = 0; i < methods.length; i++) {
      params[i] = methodToParam(klass, methods[i]);
    }
    return Params.params(params);
  }

  private static Param methodToParam(Class<?> klass, Method method)
      throws PluginImplementationException {
    if (method.getParameterTypes().length != 0) {
      throw new ParamMethodHasArgumentsException(klass, method);
    }
    Class<?> javaType = method.getReturnType();
    Type type = Type.toType(javaType);
    if (type == null) {
      throw new ForbiddenParamTypeException(klass, method, javaType);
    }

    return Param.param(type, method.getName());
  }
}
